package com.example.api2024.service;

import com.example.api2024.entity.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import com.example.api2024.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissaoService {

    private final PermissaoRepository permissaoRepository;
    private final PermissaoArquivoRepository permissaoArquivoRepository;
    private final AdmRepository admRepository;
    private final ProjetoRepository projetoRepository;
    private final ArquivoRepository arquivoRepository;
    private final ObjectMapper objectMapper;
    private final HistoricoService historicoService;

    // Método para criar uma solicitação com ou sem arquivos
    public Permissao criarSolicitacaoComArquivos(Long adminSolicitanteId, String statusSolicitado,
                                                 String informacaoProjeto, String tipoAcao,
                                                 MultipartFile propostas, MultipartFile contratos, MultipartFile artigos) throws IOException {
        Permissao permissao = criarSolicitacao(adminSolicitanteId, statusSolicitado, informacaoProjeto, tipoAcao);
        salvarArquivoPermissao(propostas, permissao, "Propostas");
        salvarArquivoPermissao(contratos, permissao, "Contratos");
        salvarArquivoPermissao(artigos, permissao, "Artigos");
        return permissao;
    }

    // Método para criar uma solicitação básica
    public Permissao criarSolicitacao(Long adminSolicitanteId, String statusSolicitado,
                                      String informacaoProjeto, String tipoAcao) {
        Permissao permissao = new Permissao();
        permissao.setAdminSolicitanteId(adminSolicitanteId);
        permissao.setStatusSolicitado(statusSolicitado);
        permissao.setDataSolicitacao(LocalDate.now());
        permissao.setInformacaoProjeto(informacaoProjeto);
        permissao.setTipoAcao(tipoAcao);
        return permissaoRepository.save(permissao);
    }

    // Salvar arquivos relacionados a uma permissão
    public void salvarArquivoPermissao(MultipartFile file, Permissao permissao, String tipoDocumento) throws IOException {
        if (file != null && !file.isEmpty()) {
            PermissaoArquivo arquivo = new PermissaoArquivo();
            arquivo.setNomeArquivo(file.getOriginalFilename());
            arquivo.setTipoArquivo(file.getContentType());
            arquivo.setConteudo(file.getBytes());
            arquivo.setTipoDocumento(tipoDocumento);
            arquivo.setPermissao(permissao);
            arquivo.setDataUpload(LocalDate.now());
            permissaoArquivoRepository.save(arquivo);
        }
    }

    // Método para criar uma solicitação de edição de projeto
    public Permissao solicitarEdicaoProjeto(Long adminSolicitanteId, String statusSolicitado, Long projetoId,
                                            String informacaoProjeto, String tipoAcao) {
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado com ID: " + projetoId));

        Permissao permissao = new Permissao();
        permissao.setAdminSolicitanteId(adminSolicitanteId);
        permissao.setStatusSolicitado(statusSolicitado);
        permissao.setDataSolicitacao(LocalDate.now());
        permissao.setInformacaoProjeto(informacaoProjeto);
        permissao.setTipoAcao(tipoAcao);
        permissao.setProjeto(projeto);

        return permissaoRepository.save(permissao);
    }
    // Aceitar uma solicitação de criação, edição ou exclusão
    @Transactional
    public Permissao aceitarSolicitacao(Long permissaoId, Long adminAprovadorId) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Permissao permissao = permissaoRepository.findById(permissaoId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));
        Adm adminAprovador = admRepository.findById(adminAprovadorId)
                .orElseThrow(() -> new IllegalArgumentException("Administrador não encontrado"));

        if (!"Pendente".equals(permissao.getStatusSolicitado())) {
            throw new IllegalStateException("A solicitação já foi processada");
        }

        Projeto projeto = null;

        if (permissao.getInformacaoProjeto() != null) {
            try {
                projeto = objectMapper.readValue(permissao.getInformacaoProjeto(), Projeto.class);
            } catch (Exception e) {
                throw new IllegalArgumentException("Erro ao processar informações do projeto", e);
            }
        }

        Projeto projetoAntigo = permissao.getProjeto();

        // Tratamento de Criação
        if ("Criacao".equals(permissao.getTipoAcao()) && projeto != null) {
            projeto.setAdm(adminAprovador);
            projeto.setSituacao(projeto.getDataTermino().isAfter(LocalDate.now()) ? "Em Andamento" : "Encerrado");
            Projeto novoProjeto = projetoRepository.save(projeto);
            transferirArquivosParaProjeto(permissao, novoProjeto);
            permissao.setProjeto(novoProjeto);

            historicoService.cadastrarHistorico(
                    permissao.getAdminSolicitanteId(),
                    "criacao",
                    "projeto",
                    novoProjeto.getId(),
                    null,
                    objectMapper.writeValueAsString(novoProjeto)
            );
        };
        // Tratamento de Edição
        if ("Editar".equals(permissao.getTipoAcao()) && projeto != null) {
            Projeto projetoExistente = permissao.getProjeto();
            if (projetoExistente != null) {
                // Atualizar o projeto existente com os novos dados
                atualizarProjeto(projetoExistente, projeto);

                // Salvar o projeto atualizado no banco de dados
                projetoExistente.setAdm(adminAprovador);
                projetoExistente.setSituacao(projeto.getDataTermino().isAfter(LocalDate.now()) ? "Em Andamento" : "Encerrado");
                projetoRepository.save(projetoExistente);

                // Transferir arquivos se existirem
                transferirArquivosParaProjeto(permissao, projetoExistente);

                historicoService.cadastrarHistorico(
                        permissao.getAdminSolicitanteId(),
                        "edicao",
                        "projeto",
                        projetoExistente.getId(),
                        objectMapper.writeValueAsString(projetoAntigo),
                        objectMapper.writeValueAsString(projeto)
                );
            }
        }
        // Tratamento de Exclusão
        else if ("Exclusao".equals(permissao.getTipoAcao()) && permissao.getProjeto() != null) {
            Projeto projetoParaExcluir = permissao.getProjeto();
            arquivoRepository.deleteByProjetoId(projetoParaExcluir.getId());
            projetoRepository.delete(projetoParaExcluir);
        }

        // Atualizar status da permissão
        permissao.setStatusSolicitado("Aprovado");
        permissao.setDataAprovado(LocalDate.now());
        permissao.setAdm(adminAprovador);

        historicoService.cadastrarHistorico(
                permissao.getAdminSolicitanteId(),
                "delecao",
                "projeto",
                projetoAntigo.getId(),
                objectMapper.writeValueAsString(projetoAntigo),
                null
        );
        return permissaoRepository.save(permissao);
    }

    private void atualizarProjeto(Projeto projetoExistente, Projeto projetoAtualizado) {
        projetoExistente.setReferenciaProjeto(projetoAtualizado.getReferenciaProjeto());
        projetoExistente.setEmpresa(projetoAtualizado.getEmpresa());
        projetoExistente.setObjeto(projetoAtualizado.getObjeto());
        projetoExistente.setDescricao(projetoAtualizado.getDescricao());
        projetoExistente.setCoordenador(projetoAtualizado.getCoordenador());
        projetoExistente.setOcultarValor(projetoAtualizado.getOcultarValor());
        projetoExistente.setOcultarEmpresa(projetoAtualizado.getOcultarEmpresa());
        projetoExistente.setValor(projetoAtualizado.getValor());
        projetoExistente.setDataInicio(projetoAtualizado.getDataInicio());
        projetoExistente.setDataTermino(projetoAtualizado.getDataTermino());
        projetoExistente.setSituacao(projetoAtualizado.getDataTermino().isAfter(LocalDate.now()) ? "Em Andamento" : "Encerrado");
    }



    private void transferirArquivosParaProjeto(Permissao permissao, Projeto projeto) {
        List<PermissaoArquivo> arquivosPermissao = permissaoArquivoRepository.findByPermissaoId(permissao.getId());
        for (PermissaoArquivo arquivoPermissao : arquivosPermissao) {
            Arquivo novoArquivo = new Arquivo();
            novoArquivo.setNomeArquivo(arquivoPermissao.getNomeArquivo());
            novoArquivo.setTipoArquivo(arquivoPermissao.getTipoArquivo());
            novoArquivo.setConteudo(arquivoPermissao.getConteudo());
            novoArquivo.setTipoDocumento(arquivoPermissao.getTipoDocumento());
            novoArquivo.setProjeto(projeto);
            arquivoRepository.save(novoArquivo);
        }
    }

    // Listar todas as solicitações pendentes
    public List<Permissao> listarPedidosPendentes() {
        return permissaoRepository.findByStatusSolicitado("Pendente");
    }

    public Permissao negarSolicitacao(Long permissaoId, Long adminAprovadorId) {
        Permissao permissao = permissaoRepository.findById(permissaoId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));

        Adm adminAprovador = admRepository.findById(adminAprovadorId)
                .orElseThrow(() -> new IllegalArgumentException("Administrador aprovador não encontrado"));

        // Verificar se o pedido já foi processado
        if (!"Pendente".equals(permissao.getStatusSolicitado())) {
            throw new IllegalStateException("A solicitação já foi processada");
        }

        // Atualizar status para "Negado" e definir data de aprovação
        permissao.setStatusSolicitado("Negado");
        permissao.setDataAprovado(LocalDate.now());
        permissao.setAdm(adminAprovador);

        // Salvar a alteração
        return permissaoRepository.save(permissao);
    }
}
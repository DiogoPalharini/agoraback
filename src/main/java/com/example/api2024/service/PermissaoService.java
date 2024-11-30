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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    @Transactional
    public Permissao aceitarSolicitacao(Long permissaoId, Long adminAprovadorId) throws JsonProcessingException {
        // Buscar a solicitação e o administrador aprovador
        Permissao permissao = permissaoRepository.findById(permissaoId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));
        Adm adminAprovador = admRepository.findById(adminAprovadorId)
                .orElseThrow(() -> new IllegalArgumentException("Administrador não encontrado"));
        Long adminSolicitante = permissao.getAdminSolicitanteId();

        // Verificar o status da solicitação
        if (!"Pendente".equals(permissao.getStatusSolicitado())) {
            throw new IllegalStateException("A solicitação já foi processada");
        }

        // Variável para o projeto a ser criado ou atualizado
        Projeto projeto = null;

        // Processar o JSON da solicitação para extrair o projeto
        if (permissao.getInformacaoProjeto() != null) {
            try {
                projeto = objectMapper.readValue(permissao.getInformacaoProjeto(), Projeto.class);

            } catch (Exception e) {
                throw new IllegalArgumentException("Erro ao processar informações do projeto: " + e.getMessage(), e);
            }
        }

        // Verificar o tipo de ação e processar
        if ("Criacao".equals(permissao.getTipoAcao()) && projeto != null) {
            // Configurar campos do projeto antes de salvar
            projeto.setAdm(adminAprovador);
            projeto.setSituacao(projeto.getDataTermino().isAfter(LocalDate.now()) ? "Em Andamento" : "Encerrado");

            // Salvar o novo projeto
            Projeto novoProjeto = projetoRepository.save(projeto);

            // Transferir arquivos relacionados à permissão para o projeto
            List <Long> jsonIds = transferirArquivosParaProjeto(permissao, novoProjeto);

            // Associar o projeto à permissão
            permissao.setProjeto(novoProjeto);

            historicoService.cadastrarHistorico(
                    adminSolicitante,
                    "criacao",
                    "projeto",
                    novoProjeto.getId(),
                    objectMapper.writeValueAsString(novoProjeto),
                    jsonIds.toString()
            );

        } else if ("Editar".equals(permissao.getTipoAcao()) && projeto != null) {
            // Buscar o projeto existente associado à permissão
            Projeto projetoExistente = permissao.getProjeto();
            if (projetoExistente != null) {
                // Atualizar os campos do projeto existente
                atualizarProjeto(projetoExistente, projeto);

                // Configurar campos do projeto antes de salvar
                projetoExistente.setAdm(adminAprovador);
                projetoExistente.setSituacao(projeto.getDataTermino().isAfter(LocalDate.now()) ? "Em Andamento" : "Encerrado");

                // Salvar o projeto atualizado
                Projeto novoProjeto = projetoRepository.save(projetoExistente);

                Historico ultimoHistorico = historicoService.buscarUltimoHistoricoPorIdAlterado(novoProjeto.getId(), "projeto");
                String idsArquivosAntigos = ultimoHistorico.getArquivos();

                // Convertendo arquivos antigos em List <Long> para poder incrementar a lista
                List<Long> listaArquivosAntigos = Arrays.stream(idsArquivosAntigos.replaceAll("[\\[\\]]", "").split(","))
                        .filter(id -> !id.isBlank()) // Evitar erros com entradas vazias
                        .map(String::trim)
                        .map(Long::parseLong)
                        .collect(Collectors.toList());

                // Transferir arquivos relacionados à permissão para o projeto atualizado
                List <Long> listaArquivosNovos = transferirArquivosParaProjeto(permissao, projetoExistente);

                // Incrementar a lista de arquivos antigos com os novos arquivos
                listaArquivosAntigos.addAll(listaArquivosNovos);
                String listaArquivosAtualizados = listaArquivosAntigos.toString();

                historicoService.cadastrarHistorico(
                        adminSolicitante,
                        "edicao",
                        "projeto",
                        projetoExistente.getId(),
                        objectMapper.writeValueAsString(novoProjeto),
                        listaArquivosAtualizados
                );
            } else {
                throw new IllegalArgumentException("Projeto associado à solicitação não encontrado.");
            }

        } else if ("Exclusao".equals(permissao.getTipoAcao()) && permissao.getProjeto() != null) {
            // Excluir o projeto associado e seus arquivos
            Projeto projetoParaExcluir = permissao.getProjeto();

            historicoService.cadastrarHistorico(
                    permissao.getAdminSolicitanteId(),
                    "delecao",
                    "projeto",
                    projetoParaExcluir.getId(),
                    null,
                    null
            );

            List<Arquivo> arquivos = arquivoRepository.findByProjetoId(projetoParaExcluir.getId());
            if (!arquivos.isEmpty()) {
                for (Arquivo arquivo : arquivos) {
                    arquivoRepository.deleteProjetoId(arquivo.getId());
                }
            }

            projetoRepository.delete(projetoParaExcluir);
        } else {
            throw new IllegalArgumentException("Tipo de ação desconhecido ou informações insuficientes para processar.");
        }

        // Atualizar o status da permissão
        permissao.setStatusSolicitado("Aprovado");
        permissao.setDataAprovado(LocalDate.now());
        permissao.setAdm(adminAprovador);

        // Salvar a permissão atualizada
        return permissaoRepository.save(permissao);
    }


    private void atualizarProjeto(Projeto projetoExistente, Projeto projetoAtualizado) {
        projetoExistente.setReferenciaProjeto(projetoAtualizado.getReferenciaProjeto());
        projetoExistente.setNome(projetoAtualizado.getNome());
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




    private List<Long> transferirArquivosParaProjeto(Permissao permissao, Projeto projeto) {
        List<PermissaoArquivo> arquivosPermissao = permissaoArquivoRepository.findByPermissaoId(permissao.getId());
        List<Long> ids = new ArrayList<>();
        for (PermissaoArquivo arquivoPermissao : arquivosPermissao) {
            Arquivo novoArquivo = new Arquivo();
            novoArquivo.setNomeArquivo(arquivoPermissao.getNomeArquivo());
            novoArquivo.setTipoArquivo(arquivoPermissao.getTipoArquivo());
            novoArquivo.setConteudo(arquivoPermissao.getConteudo());
            novoArquivo.setTipoDocumento(arquivoPermissao.getTipoDocumento());
            novoArquivo.setProjeto(projeto);
            arquivoRepository.save(novoArquivo);
            ids.add(novoArquivo.getId());
        }
        return ids;
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
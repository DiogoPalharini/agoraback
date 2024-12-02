package com.example.api2024.service;

import com.example.api2024.dto.ArquivoDto;
import com.example.api2024.dto.ProjetoDto;
import com.example.api2024.entity.*;
import com.example.api2024.repository.ArquivoRepository;
import com.example.api2024.repository.PermissaoRepository;
import com.example.api2024.repository.ProjetoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Service
public class ProjetoService {

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private ArquivoRepository arquivoRepository;

    @Autowired
    private ArquivoService arquivoService;

    @Autowired
    private PermissaoRepository permissaoRepository;

    @Autowired
    private AdmService admService;

    @Autowired
    private HistoricoService historicoService;

    @Autowired
    private ObjectMapper objectMapper;


    public Projeto buscarProjetoPorId(Long id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));
    }
    
    public Projeto buscarProjetoPorReferencia(String referenciaProjeto) {
        return projetoRepository.findByReferenciaProjeto(referenciaProjeto);
    }

    public void cadastrarProjeto(ProjetoDto projetoDto, MultipartFile propostas, MultipartFile contratos, MultipartFile artigos) throws Exception {
        Projeto projeto = new Projeto();

        // Validar o formato da referência
        String referencia = projetoDto.getReferenciaProjeto();
        if (!referencia.matches("\\d{3}/\\d{2}")) {
            throw new RuntimeException("A referência deve estar no formato XXX/YY.");
        }

        // Validar unicidade da referência
        if (projetoRepository.existsByReferenciaProjeto(referencia)) {
            throw new RuntimeException("A referência fornecida já está em uso.");
        }

        // Populando os dados do projeto
        projeto.setReferenciaProjeto(referencia);
        projeto.setNome(projetoDto.getNome());
        projeto.setEmpresa(projetoDto.getEmpresa());
        projeto.setObjeto(projetoDto.getObjeto());
        projeto.setDescricao(projetoDto.getDescricao());
        projeto.setCoordenador(projetoDto.getCoordenador());
        projeto.setValor(projetoDto.getValor());
        projeto.setOcultarEmpresa(projetoDto.getOcultarEmpresa());
        projeto.setOcultarValor(projetoDto.getOcultarValor());
        projeto.setDataInicio(projetoDto.getDataInicio());
        projeto.setDataTermino(projetoDto.getDataTermino());
        projeto.setSituacao(projetoDto.getSituacao());

        // Verificação do administrador
        Adm administrador = admService.buscarAdm(projetoDto.getAdm())
                .orElseThrow(() -> new RuntimeException("Administrador não encontrado com ID: " + projetoDto.getAdm()));
        projeto.setAdm(administrador);

        // Salvando o projeto
        Projeto savedProjeto = projetoRepository.save(projeto);

        List<Long> ids = new ArrayList<>();

        // Salvando os arquivos e adicionando os IDs à lista se não forem nulos
        Long idPropostas = null;
        if (propostas != null) {
            Arquivo arquivoPropostas = arquivoService.salvarArquivo(propostas, projeto, "Propostas", true);
            if (arquivoPropostas != null) {
                idPropostas = arquivoPropostas.getId(); // Pegue o ID do arquivo retornado
                ids.add(idPropostas); // Adicione o ID à lista
            }
        }

        Long idContratos = null;
        if (contratos != null) {
            Arquivo arquivoContratos = arquivoService.salvarArquivo(contratos, projeto, "Contratos", true);
            if (arquivoContratos != null) {
                idContratos = arquivoContratos.getId();
                ids.add(idContratos);
            }
        }

        Long idArtigos = null;
        if (artigos != null) {
            Arquivo arquivoArtigos = arquivoService.salvarArquivo(artigos, projeto, "Artigos", true);
            if (arquivoArtigos != null) {
                idArtigos = arquivoArtigos.getId();
                ids.add(idArtigos);
            }
        }


        // Converter a lista para JSON e armazenar no banco
        String jsonIds = ids.toString();

        historicoService.cadastrarHistorico(
                administrador.getId(),
                "criacao",
                "projeto",
                savedProjeto.getId(),
                objectMapper.writeValueAsString(projeto),
                jsonIds
        );
    }

    // Método para listar todos os projetos
    public List<Projeto> listarProjetos() {
        return projetoRepository.findAll();
    }

    // Método para salvar arquivos
    public Arquivo salvarArquivo(MultipartFile file, Projeto projeto, String tipoDocumento) throws IOException {
        if (file != null && !file.isEmpty()) {
            Arquivo arquivo = new Arquivo();
            arquivo.setNomeArquivo(file.getOriginalFilename());
            arquivo.setTipoArquivo(file.getContentType());
            arquivo.setConteudo(file.getBytes());
            arquivo.setTipoDocumento(tipoDocumento);
            arquivo.setProjeto(projeto);
            arquivo.setAprovado(true); // Sempre marca como aprovado ao adicionar
            return arquivoRepository.save(arquivo);
        }
        return null;
    }


    public List<Long> atualizarListaArquivos(String idsArquivosAntigos, String idsArquivosExcluidos) {
        // Converter a string de IDs de arquivos antigos para uma lista de Long
        List<Long> listaArquivosAntigos = Arrays.stream(idsArquivosAntigos.replaceAll("[\\[\\]]", "").split(","))
                .filter(id -> !id.isBlank()) // Evitar erros com entradas vazias
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());

        // Se a lista de excluídos não for nula e não estiver vazia, processar
        if (idsArquivosExcluidos != null && !idsArquivosExcluidos.isEmpty()) {
            List<Long> listaArquivosExcluidos = Arrays.stream(idsArquivosExcluidos.replaceAll("[\\[\\]]", "").split(","))
                    .filter(id -> !id.isBlank())
                    .map(String::trim)
                    .map(Long::parseLong)
                    .toList();

            // Remover da lista de antigos os IDs que estão na lista de excluídos
            listaArquivosAntigos.removeAll(listaArquivosExcluidos);
        }

        // Retornar a lista atualizada
        return listaArquivosAntigos;
    }


    // Método para editar projeto
    @Transactional
    public Projeto editarProjeto(
            Long id,
            ProjetoDto projetoDto,
            MultipartFile propostas,
            MultipartFile contratos,
            MultipartFile artigos,
            List<Long> arquivosExcluidos) throws IOException {

        Projeto projetoExistente = projetoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado com ID: " + id));

        // Atualizar informações do projeto
        projetoExistente.setReferenciaProjeto(projetoDto.getReferenciaProjeto());
        projetoExistente.setNome(projetoDto.getNome());
        projetoExistente.setEmpresa(projetoDto.getEmpresa());
        projetoExistente.setObjeto(projetoDto.getObjeto());
        projetoExistente.setDescricao(projetoDto.getDescricao());
        projetoExistente.setCoordenador(projetoDto.getCoordenador());
        projetoExistente.setOcultarValor(projetoDto.getOcultarValor());
        projetoExistente.setOcultarEmpresa(projetoDto.getOcultarEmpresa());
        projetoExistente.setValor(projetoDto.getValor());
        projetoExistente.setDataInicio(projetoDto.getDataInicio());
        projetoExistente.setDataTermino(projetoDto.getDataTermino());

        // Verificar situação do projeto
        String situacao = projetoDto.getDataTermino().isAfter(LocalDate.now()) ? "Em Andamento" : "Encerrado";
        projetoExistente.setSituacao(situacao);

        // Desativar arquivos se necessário
        if (arquivosExcluidos != null && !arquivosExcluidos.isEmpty()) {
            for (Long arquivoId : arquivosExcluidos) {
                arquivoService.desativarArquivo(arquivoId); // Exclusão lógica
            }
        }

        // Adicionar novos arquivos
        if (propostas != null) {
            arquivoService.salvarArquivo(propostas, projetoExistente, "Propostas", true);
        }
        if (contratos != null) {
            arquivoService.salvarArquivo(contratos, projetoExistente, "Contratos", true);
        }
        if (artigos != null) {
            arquivoService.salvarArquivo(artigos, projetoExistente, "Artigos", true);
        }

        return projetoRepository.save(projetoExistente);
    }




    // Método para excluir um projeto e seus arquivos associados
    @Transactional
    public void excluirProjeto(Long id, Long admAlterador) {
        // Buscar o projeto
        Projeto projeto = buscarProjetoPorId(id);

        // Atualizar o status do projeto (ou outro campo que indique exclusão lógica)
        projeto.setSituacao("Desativado");
        projetoRepository.save(projeto);

        // Atualizar os arquivos relacionados para "desativados"
        List<Arquivo> arquivos = arquivoRepository.findByProjetoId(projeto.getId());
        if (!arquivos.isEmpty()) {
            arquivos.forEach(arquivo -> arquivo.setAprovado(false)); // Marca os arquivos como não aprovados
            arquivoRepository.saveAll(arquivos); // Salva as alterações no banco
        }

        // Registrar no histórico a desativação do projeto
        historicoService.cadastrarHistorico(
                admAlterador,
                "desativacao",
                "projeto",
                projeto.getId(),
                null,
                null
        );
    }


    public String calcularProximaReferencia() {
        int anoAtual = LocalDate.now().getYear() % 100;
        List<Integer> numerosUtilizados = projetoRepository.findAll()
            .stream()
            .map(Projeto::getReferenciaProjeto)
            .filter(ref -> ref.endsWith("/" + anoAtual))
            .map(ref -> Integer.parseInt(ref.split("/")[0]))
            .sorted()
            .toList();

        for (int i = 1; i <= 999; i++) {
            if (!numerosUtilizados.contains(i)) {
                return String.format("%03d/%02d", i, anoAtual);
            }
        }
        throw new RuntimeException("Todas as referências para o ano " + anoAtual + " estão ocupadas.");
    }
}

	

package com.example.api2024.service;

import com.example.api2024.dto.ArquivoDto;
import com.example.api2024.entity.Arquivo;
import com.example.api2024.entity.Projeto;
import com.example.api2024.repository.ArquivoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArquivoService {

    private final ArquivoRepository arquivoRepository;

    // Buscar arquivos por ID do projeto
    public List<ArquivoDto> getArquivosByProjetoId(Long projetoId) {
        List<Arquivo> arquivos = arquivoRepository.findByProjetoIdAndAprovadoTrue(projetoId);
        return arquivos.stream()
                .map(arquivo -> new ArquivoDto(
                        arquivo.getId(),
                        arquivo.getNomeArquivo(),
                        arquivo.getTipoDocumento(),
                        arquivo.getProjeto().getId(),
                        arquivo.getTipoArquivo()
                )).collect(Collectors.toList());
    }

    // Buscar arquivo por ID
    public Optional<Arquivo> getArquivoById(Long arquivoId) {
        return arquivoRepository.findById(arquivoId);
    }

    // Salvar arquivo (definir aprovado como true)
    public Arquivo salvarArquivo(MultipartFile file, Projeto projeto, String tipoDocumento, boolean aprovado) throws IOException {
        if (file != null && !file.isEmpty()) {
            Arquivo arquivo = new Arquivo();
            arquivo.setNomeArquivo(file.getOriginalFilename());
            arquivo.setTipoArquivo(file.getContentType());
            arquivo.setConteudo(file.getBytes());
            arquivo.setTipoDocumento(tipoDocumento);
            arquivo.setProjeto(projeto);
            arquivo.setAprovado(aprovado); // Define o estado de aprovação
            return arquivoRepository.save(arquivo);
        }
        return null;
    }

    // Desativar arquivo (exclusão lógica)
    public void desativarArquivo(Long arquivoId) {
        Arquivo arquivo = arquivoRepository.findById(arquivoId)
                .orElseThrow(() -> new RuntimeException("Arquivo não encontrado com ID: " + arquivoId));
        arquivo.setAprovado(false); // Atualiza o campo para exclusão lógica
        arquivoRepository.save(arquivo); // Persiste a alteração
    }

    public List<ArquivoDto> getArquivosByIds(List<Long> ids) {
        List<Arquivo> arquivos = arquivoRepository.findAllById(ids);
        return arquivos.stream()
                .map(arquivo -> {
                    Long projetoId = arquivo.getProjeto() != null ? arquivo.getProjeto().getId() : null;
                    return new ArquivoDto(
                            arquivo.getId(),
                            arquivo.getNomeArquivo(),
                            arquivo.getTipoDocumento(),
                            projetoId, // Pode ser null
                            arquivo.getTipoArquivo()
                    );
                }).toList();
    }


}

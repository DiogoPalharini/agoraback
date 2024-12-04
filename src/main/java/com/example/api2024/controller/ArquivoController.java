package com.example.api2024.controller;

import com.example.api2024.dto.ArquivoDto;
import com.example.api2024.entity.Arquivo;
import com.example.api2024.service.ArquivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/arquivos")
@RequiredArgsConstructor
public class ArquivoController {

    private final ArquivoService arquivoService;

    // Busca arquivos associados a um projeto
    @GetMapping("/projeto/{projetoId}")
    public ResponseEntity<List<ArquivoDto>> getArquivosByProjetoId(@PathVariable Long projetoId) {
        List<ArquivoDto> arquivos = arquivoService.getArquivosByProjetoId(projetoId);
        return ResponseEntity.ok(arquivos);
    }

    // Realiza o download de um arquivo específico
    @GetMapping("/download/{arquivoId}")
    public ResponseEntity<ByteArrayResource> downloadArquivo(@PathVariable Long arquivoId) {
        Arquivo arquivo = arquivoService.getArquivoById(arquivoId)
                .orElseThrow(() -> new RuntimeException("Arquivo não encontrado"));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(arquivo.getTipoArquivo()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + arquivo.getNomeArquivo() + "\"")
                .body(new ByteArrayResource(arquivo.getConteudo()));
    }

    // Busca arquivos a partir de uma lista de IDs
    @GetMapping("/buscar-por-ids")
    public ResponseEntity<List<ArquivoDto>> getArquivosByIds(@RequestParam List<Long> ids) {
        List<ArquivoDto> arquivos = arquivoService.getArquivosByIds(ids);
        return ResponseEntity.ok(arquivos);
    }
}

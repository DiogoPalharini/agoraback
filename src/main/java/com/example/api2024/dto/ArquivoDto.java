package com.example.api2024.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArquivoDto {
    private Long id;
    private String nomeArquivo;
    private String tipoDocumento;
    private Long projetoId;
    private String tipoArquivo;

    // O conteúdo do arquivo será incluído apenas quando necessário
    private byte[] conteudo;

    public ArquivoDto(Long id, String nomeArquivo, String tipoDocumento, Long projetoId, String tipoArquivo) {
        this.id = id;
        this.nomeArquivo = nomeArquivo;
        this.tipoDocumento = tipoDocumento;
        this.projetoId = projetoId;
        this.tipoArquivo = tipoArquivo;
    }
}

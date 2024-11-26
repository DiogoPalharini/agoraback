package com.example.api2024.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class HistoricoDto {
    private Long id;
    private Long admAlterador;
    private String alteracao;
    private String alterado;
    private Long idAlterado;
    private String dadosAntigos;
    private String dadosNovos;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dataAlteracao;
}

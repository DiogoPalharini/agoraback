package com.example.api2024.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@Data
public class AnaliseProjeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal valorGasto;

    @Column(nullable = false)
    private String autor;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String informacoesAdicionais;

    @Column(nullable = false)
    private Boolean resultadoObtido;

    @ManyToOne
    @JoinColumn(name = "idProjeto", nullable = false)
    private Projeto projeto;
}
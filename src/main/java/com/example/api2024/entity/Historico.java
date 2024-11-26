package com.example.api2024.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Data
public class Historico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long admAlterador;
    
    @Column(nullable = false)
    private String alteracao;

    @Column(nullable = false)
    private String alterado;

    @Column(nullable = false)
    private Long idAlterado;

    @Lob
    @Column(nullable = true, columnDefinition = "TEXT")
    private String dadosAntigos;

    @Lob
    @Column(nullable = true, columnDefinition = "TEXT")
    private String dadosNovos;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataAlteracao = LocalDate.now();
}

package com.example.api2024.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

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
    private String arquivos;

    @Lob
    @Column(nullable = true, columnDefinition = "TEXT")
    private String dados;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataAlteracao = LocalDate.now();
}

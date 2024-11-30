package com.example.api2024.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Arquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String nomeArquivo;

    @Column(nullable = false)
    private String tipoArquivo;

    @Lob
    @Column(name = "conteudo", columnDefinition = "LONGBLOB")
    private byte[] conteudo;

    @Column(nullable = false)
    private String tipoDocumento;

    @Column(nullable = false)
    private boolean aprovado; // Certifique-se de que este campo está presente

    @ManyToOne
    @JoinColumn(name = "projeto_id", nullable = true)
    private Projeto projeto;
}

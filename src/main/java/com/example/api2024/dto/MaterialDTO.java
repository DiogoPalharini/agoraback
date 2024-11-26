package com.example.api2024.dto;

import java.math.BigDecimal;

public class MaterialDTO {
    private Long id;
    private String nome;
    private String nomeProjeto; // Receberá "referenciaProjeto - nome"
    private Integer quantidadeUsada;
    private BigDecimal valor;
    private String fornecedor;
    private String descricao;

    // Construtor usado na query
    public MaterialDTO(Long id, String nome, String referenciaProjeto, String nomeProjeto, Integer quantidadeUsada, BigDecimal valor, String fornecedor, String descricao) {
        this.id = id;
        this.nome = nome;
        this.nomeProjeto = (referenciaProjeto != null && nomeProjeto != null)
                ? referenciaProjeto + " - " + nomeProjeto
                : "Projeto não associado"; // Valor padrão caso `projetoAssociado` seja nulo
        this.quantidadeUsada = quantidadeUsada;
        this.valor = valor;
        this.fornecedor = fornecedor;
        this.descricao = descricao;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeProjeto() {
        return nomeProjeto;
    }

    public void setNomeProjeto(String nomeProjeto) {
        this.nomeProjeto = nomeProjeto;
    }

    public Integer getQuantidadeUsada() {
        return quantidadeUsada;
    }

    public void setQuantidadeUsada(Integer quantidadeUsada) {
        this.quantidadeUsada = quantidadeUsada;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}

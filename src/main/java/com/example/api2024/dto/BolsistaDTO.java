package com.example.api2024.dto;

import java.math.BigDecimal;

public class BolsistaDTO {

    private Long id;
    private String nome;
    private String areaAtuacao;
    private Long projetoId;
    private String convenio;
    private String cidade;
    private String cpf;
    private String telefone;
    private BigDecimal valorBolsa;
    private Integer duracaoBolsa;

    // Construtor
    public BolsistaDTO(Long id, String nome, String areaAtuacao, Long projetoId, String convenio, String cidade, String cpf, String telefone, BigDecimal valorBolsa, Integer duracaoBolsa) {
        this.id = id;
        this.nome = nome;
        this.areaAtuacao = areaAtuacao;
        this.projetoId = projetoId;
        this.convenio = convenio;
        this.cidade = cidade;
        this.cpf = cpf;
        this.telefone = telefone;
        this.valorBolsa = valorBolsa;
        this.duracaoBolsa = duracaoBolsa;
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

    public String getAreaAtuacao() {
        return areaAtuacao;
    }

    public void setAreaAtuacao(String areaAtuacao) {
        this.areaAtuacao = areaAtuacao;
    }

    public Long getProjetoId() {
        return projetoId;
    }

    public void setProjetoId(Long projetoId) {
        this.projetoId = projetoId;
    }

    public String getConvenio() {
        return convenio;
    }

    public void setConvenio(String convenio) {
        this.convenio = convenio;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public BigDecimal getValorBolsa() {
        return valorBolsa;
    }

    public void setValorBolsa(BigDecimal valorBolsa) {
        this.valorBolsa = valorBolsa;
    }

    public Integer getDuracaoBolsa() {
        return duracaoBolsa;
    }

    public void setDuracaoBolsa(Integer duracaoBolsa) {
        this.duracaoBolsa = duracaoBolsa;
    }
}
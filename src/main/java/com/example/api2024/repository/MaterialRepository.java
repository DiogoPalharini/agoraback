package com.example.api2024.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.api2024.dto.MaterialDTO;
import com.example.api2024.entity.Material;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    Optional<Material> findByNome(String nome);
    @Query("SELECT SUM(m.valor * m.quantidadeUsada) FROM Material m")
    BigDecimal calcularValorTotal();

    @Query("SELECT m.projetoAssociado.referenciaProjeto, SUM(m.quantidadeUsada) FROM Material m GROUP BY m.projetoAssociado.referenciaProjeto")
    List<Object[]> listarMateriaisPorProjeto();    

    @Query("SELECT m.fornecedor, SUM(m.quantidadeUsada) FROM Material m GROUP BY m.fornecedor")
    List<Object[]> listarQuantidadePorFornecedor();

    @Query("SELECT new com.example.api2024.dto.MaterialDTO(m.id, m.nome, p.referenciaProjeto, p.nome, m.quantidadeUsada, m.valor, m.fornecedor, m.descricao) " +
           "FROM Material m LEFT JOIN m.projetoAssociado p")
    List<MaterialDTO> listarMateriaisComProjeto();
}
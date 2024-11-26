package com.example.api2024.repository;

import com.example.api2024.entity.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjetoRepository extends JpaRepository<Projeto, Long> {
    boolean existsByReferenciaProjeto(String referenciaProjeto);
    Projeto findByReferenciaProjeto(String referenciaProjeto);
}

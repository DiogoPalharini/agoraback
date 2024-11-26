package com.example.api2024.repository;

import com.example.api2024.entity.Convenio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConvenioRepository extends JpaRepository<Convenio, Long> {

    @Query("SELECT c.tipoConvenio, COUNT(c.instituicao) FROM Convenio c GROUP BY c.tipoConvenio")
    List<Object[]> countInstituicoesByTipoConvenio();
}
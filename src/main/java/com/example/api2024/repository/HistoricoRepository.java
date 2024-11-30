package com.example.api2024.repository;

import com.example.api2024.entity.Historico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface HistoricoRepository extends JpaRepository<Historico, Long> {
    @Query("SELECT h FROM Historico h WHERE h.idAlterado = :idAlterado AND h.alterado = :alterado ORDER BY h.id DESC")
    List<Historico> findHistoricosByIdAlterado(@Param("idAlterado") Long idAlterado, @Param("alterado") String alterado);
}

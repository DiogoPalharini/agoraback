package com.example.api2024.controller;

import com.example.api2024.entity.Historico;
import com.example.api2024.service.HistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historico")
public class HistoricoController {

    @Autowired
    private HistoricoService historicoService;

    @GetMapping("/listar")
    public List<Historico> listarHistorico() {
        return historicoService.listarHistorico();
    }

    @GetMapping("/ultimo/{idAlterado}/{alterado}")
    public ResponseEntity<?> buscarUltimoHistorico(@PathVariable Long idAlterado, @PathVariable String alterado) {
        Historico historico = historicoService.buscarUltimoHistoricoPorIdAlterado(idAlterado, alterado);
        if (historico == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarHistoricoPorId(@PathVariable Long id) {
        try {
            Historico historico = historicoService.buscarHistoricoPorId(id);
            if (historico != null) {
                return ResponseEntity.ok(historico);
            } else {
                return ResponseEntity.status(404).body("Historico não encontrado.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao buscar historico: " + e.getMessage());
        }
    }
}

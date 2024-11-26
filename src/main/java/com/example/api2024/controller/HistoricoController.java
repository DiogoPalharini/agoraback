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

    @PostMapping("/cadastrar")
    public ResponseEntity<Historico> cadastrarHistorico(@RequestBody Historico historico) {
        Historico novoHistorico = historicoService.cadastrarHistorico(
                historico.getAdmAlterador(),
                historico.getAlteracao(),
                historico.getAlterado(),
                historico.getIdAlterado(),
                historico.getDadosAntigos(),
                historico.getDadosNovos()
        );
        return ResponseEntity.ok(novoHistorico);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Historico>> listarHistorico() {
        List<Historico> historicos = historicoService.listarHistorico();
        return ResponseEntity.ok(historicos);
    }
}
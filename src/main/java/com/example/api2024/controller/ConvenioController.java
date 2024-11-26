package com.example.api2024.controller;

import com.example.api2024.entity.Convenio;
import com.example.api2024.service.ConvenioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/convenio")
public class ConvenioController {

    @Autowired
    private ConvenioService convenioService;

    @PostMapping("/criar")
    public ResponseEntity<?> criarConvenio(@RequestBody Convenio convenio) {
        try {
            Convenio novoConvenio = convenioService.criarConvenio(convenio);
            return new ResponseEntity<>(novoConvenio, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "Erro ao criar convênio: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    // Read All
    @GetMapping("/listar")
    public ResponseEntity<List<Convenio>> listarConvenios() {
        List<Convenio> convenios = convenioService.listarConvenios();
        return new ResponseEntity<>(convenios, HttpStatus.OK);
    }
    
    // Read por ID
    @GetMapping("/listar/{id}")
    public ResponseEntity<Convenio> listarConvenioPorId(@PathVariable Long id) {
        Convenio convenio = convenioService.buscarConvenioPorId(id);
        if (convenio != null) {
            return new ResponseEntity<>(convenio, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    // Update
    @PutMapping("/editar/{id}")
    public ResponseEntity<Convenio> atualizarConvenio(@PathVariable Long id, @RequestBody Convenio convenio) {
        Convenio convenioAtualizado = convenioService.atualizarConvenio(id, convenio);
        if (convenioAtualizado != null) {
            return new ResponseEntity<>(convenioAtualizado, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Delete
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarConvenio(@PathVariable Long id) {
        boolean deletado = convenioService.deletarConvenio(id);
        if (deletado) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/instituicoes-por-convenio")
    public ResponseEntity<Map<String, Long>> getInstituicoesPorConvenio() {
        Map<String, Long> response = convenioService.getInstituicoesPorTipoConvenio();
        return ResponseEntity.ok(response);
    }
}
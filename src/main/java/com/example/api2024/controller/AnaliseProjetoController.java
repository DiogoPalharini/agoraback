package com.example.api2024.controller;

import com.example.api2024.entity.AnaliseProjeto;
import com.example.api2024.entity.Projeto;
import com.example.api2024.service.AnaliseProjetoService;
import com.example.api2024.service.ProjetoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/analises")
public class AnaliseProjetoController {

    @Autowired
    private AnaliseProjetoService analiseProjetoService;
    
    @Autowired
    private ProjetoService projetoService;

    // Endpoint para criar uma análise
    @PostMapping("/criar")
    public ResponseEntity<AnaliseProjeto> criarAnalise(@RequestBody Map<String, Object> payload) {
        try {
            String referenciaProjeto = (String) payload.get("idProjeto"); // ID do projeto referenciado
            BigDecimal valorGasto = new BigDecimal(payload.get("valorGasto").toString());
            String autor = (String) payload.get("autor");
            String informacoesAdicionais = (String) payload.get("informacoesAdicionais");
            Boolean resultadoObtido = Boolean.parseBoolean(payload.get("resultadoObtido").toString());

            // Cria uma nova análise
            AnaliseProjeto analiseProjeto = new AnaliseProjeto();
            analiseProjeto.setValorGasto(valorGasto);
            analiseProjeto.setAutor(autor);
            analiseProjeto.setInformacoesAdicionais(informacoesAdicionais);
            analiseProjeto.setResultadoObtido(resultadoObtido);

            // Associa o projeto e salva a análise
            AnaliseProjeto novaAnalise = analiseProjetoService.salvarAnalise(analiseProjeto, referenciaProjeto);

            return ResponseEntity.status(201).body(novaAnalise);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    // Endpoint para listar todas as análises
    @GetMapping("/listar")
    public ResponseEntity<List<Map<String, Object>>> listarTodas() {
        try {
            List<AnaliseProjeto> analises = analiseProjetoService.listarTodas();

            // Mapeia para incluir apenas os campos necessários
            List<Map<String, Object>> analisesFormatadas = analises.stream().map(analise -> {
                Map<String, Object> analiseMap = new HashMap<>();
                analiseMap.put("id", analise.getId());
                analiseMap.put("valorGasto", analise.getValorGasto());
                analiseMap.put("autor", analise.getAutor());
                analiseMap.put("informacoesAdicionais", analise.getInformacoesAdicionais());
                analiseMap.put("resultadoObtido", analise.getResultadoObtido());
                analiseMap.put("idProjeto", analise.getProjeto().getReferenciaProjeto()); // Inclui a referência do projeto
                return analiseMap;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(analisesFormatadas);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }


    // Endpoint para buscar uma análise por ID
    @GetMapping("/listar/{id}")
    public ResponseEntity<AnaliseProjeto> buscarPorId(@PathVariable Long id) {
        AnaliseProjeto analise = analiseProjetoService.buscarPorId(id);
        return ResponseEntity.ok(analise);
    }

    // Endpoint para editar uma análise existente
    @PutMapping("/editar/{id}")
    public ResponseEntity<AnaliseProjeto> editarAnalise(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        try {
            // Busca a análise existente
            AnaliseProjeto analiseExistente = analiseProjetoService.buscarPorId(id);
            if (analiseExistente == null) {
                return ResponseEntity.status(404).body(null);
            }

            // Atualiza os campos
            BigDecimal valorGasto = new BigDecimal(payload.get("valorGasto").toString());
            String autor = (String) payload.get("autor");
            String informacoesAdicionais = (String) payload.get("informacoesAdicionais");
            Boolean resultadoObtido = Boolean.parseBoolean(payload.get("resultadoObtido").toString());
            String referenciaProjeto = (String) payload.get("idProjeto");

            // Recupera o projeto associado
            Projeto projeto = projetoService.buscarProjetoPorReferencia(referenciaProjeto);
            if (projeto == null) {
                return ResponseEntity.status(400).body(null); // Retorna erro se o projeto não for encontrado
            }

            // Atualiza os dados da análise
            analiseExistente.setValorGasto(valorGasto);
            analiseExistente.setAutor(autor);
            analiseExistente.setInformacoesAdicionais(informacoesAdicionais);
            analiseExistente.setResultadoObtido(resultadoObtido);
            analiseExistente.setProjeto(projeto); // Associa o projeto

            // Salva a análise atualizada
            AnaliseProjeto analiseAtualizada = analiseProjetoService.salvar(analiseExistente);
            return ResponseEntity.ok(analiseAtualizada);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }


    // Endpoint para excluir uma análise
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> excluirAnalise(@PathVariable Long id) {
        analiseProjetoService.excluirAnalise(id);
        return ResponseEntity.noContent().build();
    }
}

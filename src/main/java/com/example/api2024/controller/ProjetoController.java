package com.example.api2024.controller;

import com.example.api2024.dto.ProjetoDto;
import com.example.api2024.entity.Projeto;
import com.example.api2024.service.ProjetoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projeto")
@CrossOrigin(origins = "http://localhost:3000") // Permite o frontend se conectar ao backend
public class ProjetoController {

    @Autowired
    private ProjetoService projetoService;

    @Qualifier("objectMapper")
    @Autowired
    private ObjectMapper objectMapper;

    // Endpoint para listar todos os projetos
    @GetMapping("/listar")
    public List<Projeto> listarProjeto() {
        return projetoService.listarProjetos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarProjetoPorId(@PathVariable Long id) {
        try {
            Projeto projeto = projetoService.buscarProjetoPorId(id);
            if (projeto != null) {
                return ResponseEntity.ok(projeto);
            } else {
                return ResponseEntity.status(404).body("Projeto não encontrado.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao buscar o projeto: " + e.getMessage());
        }
    }
    
    // Buscar projeto por referência
    @GetMapping("/referencia")
    public ResponseEntity<?> buscarProjetoPorReferencia(@RequestParam("referenciaProjeto") String referenciaProjeto) {
        try {
            String decodedReferencia = URLDecoder.decode(referenciaProjeto, StandardCharsets.UTF_8.name());
            System.out.println("Decoded referenciaProjeto: " + decodedReferencia);
            Projeto projeto = projetoService.buscarProjetoPorReferencia(decodedReferencia);
            if (projeto != null) {
                return ResponseEntity.ok(projeto);
            } else {
                return ResponseEntity.status(404).body("Projeto não encontrado.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao buscar o projeto: " + e.getMessage());
        }
    }

    // Endpoint para cadastrar um novo projeto
    @PostMapping("/cadastrar")
    public void cadastrarProjeto(
            @RequestPart("projeto") ProjetoDto projetoDto,
            @RequestPart(value = "propostas", required = false) MultipartFile propostas,
            @RequestPart(value = "contratos", required = false) MultipartFile contratos,
            @RequestPart(value = "artigos", required = false) MultipartFile artigos) throws Exception {
        projetoService.cadastrarProjeto(projetoDto, propostas, contratos, artigos);

    }

    // quero que você coloque mais um parametro para editar projeto, que é Long admAlterador

    // Endpoint para editar um projeto existente
    @PutMapping("/editar/{id}")
    public ResponseEntity<Projeto> editarProjeto(
            @PathVariable Long id,
            @RequestPart("projeto") String projetoJson,
            @RequestPart(value = "propostas", required = false) MultipartFile propostas,
            @RequestPart(value = "contratos", required = false) MultipartFile contratos,
            @RequestPart(value = "artigos", required = false) MultipartFile artigos,
            @RequestParam(value = "arquivosExcluidos", required = false) List<Long> arquivosExcluidos) throws IOException {

        try {
            ProjetoDto projetoDto = objectMapper.readValue(projetoJson, ProjetoDto.class);

            Projeto projetoAtualizado = projetoService.editarProjeto(id, projetoDto, propostas, contratos, artigos, arquivosExcluidos);
            return ResponseEntity.ok(projetoAtualizado);
        } catch (Exception e) {
            System.err.println("Erro ao processar a requisição: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/proxima-referencia")
    public ResponseEntity<String> obterProximaReferencia() {
        try {
            String proximaReferencia = projetoService.calcularProximaReferencia();
            return ResponseEntity.ok(proximaReferencia);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao calcular a próxima referência: " + e.getMessage());
        }
    }


    // Endpoint para excluir um projeto
    @DeleteMapping("/excluir")
    public void excluirProjeto(@RequestBody Map<String, Long> informacaoProjeto) throws JsonProcessingException {
        Long id = informacaoProjeto.get("id");
        Long admAlterador = informacaoProjeto.get("admAlterador");
        projetoService.excluirProjeto(id, admAlterador);
    }
}

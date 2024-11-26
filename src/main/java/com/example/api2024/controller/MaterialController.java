package com.example.api2024.controller;

import com.example.api2024.dto.MaterialDTO;
import com.example.api2024.entity.Material;
import com.example.api2024.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    // Create
    @PostMapping
    public ResponseEntity<Map<String, String>> criarMaterial(
            @RequestBody Material novoMaterial,
            @RequestParam Long idAdm) {

        
        // Persistir o novo material no banco de dados
        materialService.criarMaterial(novoMaterial);

        // Retornar uma resposta de sucesso
        return ResponseEntity.ok(Map.of("message", "Material criado com sucesso!"));
    }

    // Read All
    @GetMapping("/listar")
    public ResponseEntity<List<MaterialDTO>> listarMateriais() {
        List<MaterialDTO> materiais = materialService.listarMateriais();
        return ResponseEntity.ok(materiais);
    }

    
    // Read by ID
    @GetMapping("/{id}")
    public ResponseEntity<Material> listarMaterialPorId(@PathVariable Long id) {
        Material material = materialService.buscarMaterialPorId(id);
        if (material != null) {
            return ResponseEntity.ok(material);
        }
        return ResponseEntity.status(404).build(); // Retorna 404 se o material não for encontrado
    }

 
    // Update
    @PutMapping("/editar/{id}")
    public ResponseEntity<Map<String, String>> editarMaterial(
            @PathVariable Long id,
            @RequestBody Material materialAtualizado,
            @RequestParam Long idAdm) {

       
        // Verificar se o material existe e atualizá-lo
        Material materialEditado = materialService.atualizarMaterial(id, materialAtualizado);
        if (materialEditado != null) {
            return ResponseEntity.ok(Map.of("message", "Material editado com sucesso!"));
        }
        return ResponseEntity.status(404).body(Map.of("message", "Material não encontrado."));
    }

    // Delete
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Map<String, String>> deletarMaterial(
            @PathVariable Long id,
            @RequestParam Long idAdm) {

        
        boolean deletado = materialService.deletarMaterial(id);
        if (deletado) {
            return ResponseEntity.ok(Map.of("message", "Material excluído com sucesso!"));
        }
        return ResponseEntity.status(404).body(Map.of("message", "Material não encontrado."));
    }

    // Obter Valor Total Pago
    @GetMapping("/valor-total")
    public ResponseEntity<Map<String, BigDecimal>> obterValorTotalPago() {
        BigDecimal valorTotal = materialService.calcularValorTotal();
        return ResponseEntity.ok(Map.of("valorTotal", valorTotal));
    }

    // Listar Materiais por Projeto
    @GetMapping("/materiais-por-projeto")
    public ResponseEntity<Map<String, Integer>> listarMateriaisPorProjeto() {
        Map<String, Integer> materiaisPorProjeto = materialService.listarMateriaisPorProjeto();
        return ResponseEntity.ok(materiaisPorProjeto);
    }    

    // Listar Quantidade Comprada por Fornecedor
    @GetMapping("/quantidade-por-fornecedor")
    public ResponseEntity<Map<String, Integer>> listarQuantidadePorFornecedor() {
        Map<String, Integer> quantidadePorFornecedor = materialService.listarQuantidadePorFornecedor();
        return ResponseEntity.ok(quantidadePorFornecedor);
    }
}

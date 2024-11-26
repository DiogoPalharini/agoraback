package com.example.api2024.service;

import com.example.api2024.dto.MaterialDTO;
import com.example.api2024.entity.Material;
import com.example.api2024.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    // Create
    public Material criarMaterial(Material material) {
        return materialRepository.save(material);
    }
    
    // Read by ID
    public Material buscarMaterialPorId(Long id) {
        Optional<Material> material = materialRepository.findById(id);
        return material.orElse(null); // Retorna o material ou null se não for encontrado
    }

    public List<MaterialDTO> listarMateriais() {
        List<MaterialDTO> materiais = materialRepository.listarMateriaisComProjeto();
        materiais.forEach(material -> {
            System.out.println("Material ID: " + material.getId());
            System.out.println("Nome Projeto: " + material.getNomeProjeto());
        });
        return materiais;
    }


    // Update
    public Material atualizarMaterial(Long id, Material materialAtualizado) {
        Optional<Material> materialExistente = materialRepository.findById(id);
        if (materialExistente.isPresent()) {
            Material material = materialExistente.get();
            material.setNome(materialAtualizado.getNome());
            material.setProjetoAssociado(materialAtualizado.getProjetoAssociado());
            material.setQuantidadeUsada(materialAtualizado.getQuantidadeUsada());
            material.setValor(materialAtualizado.getValor());
            material.setFornecedor(materialAtualizado.getFornecedor());
            material.setDescricao(materialAtualizado.getDescricao());
            return materialRepository.save(material);
        }
        return null;
    }

    // Delete
    public boolean deletarMaterial(Long id) {
        if (materialRepository.existsById(id)) {
            materialRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Valor total pago nos materiais
    public BigDecimal calcularValorTotal() {
        return materialRepository.calcularValorTotal();
    }

    public Map<String, Integer> listarMateriaisPorProjeto() {
        List<Object[]> resultados = materialRepository.listarMateriaisPorProjeto();
        return resultados.stream().collect(Collectors.toMap(
            resultado -> (String) resultado[0], // Nome do projeto
            resultado -> ((Long) resultado[1]).intValue() // Quantidade de materiais
        ));
    }
        
    // Quantidade comprada por fornecedor
    public Map<String, Integer> listarQuantidadePorFornecedor() {
        return materialRepository.listarQuantidadePorFornecedor().stream()
                .collect(Collectors.toMap(
                        obj -> (String) obj[0],
                        obj -> ((Long) obj[1]).intValue()
                ));
    }
}

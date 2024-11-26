package com.example.api2024.service;

import com.example.api2024.dto.BolsistaDTO;
import com.example.api2024.entity.Bolsista;
import com.example.api2024.repository.BolsistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BolsistaService {

    @Autowired
    private BolsistaRepository bolsistaRepository;

    // Valor total pago em bolsas
    public BigDecimal getValorTotalBolsa() {
        return bolsistaRepository.totalValorBolsa();
    }

    // Quantidade de bolsistas por área de atuação
    public Map<String, Long> getBolsistasPorArea() {
        List<Object[]> results = bolsistaRepository.countBolsistasPorArea();
        Map<String, Long> bolsistasPorArea = new HashMap<>();

        for (Object[] result : results) {
            String areaAtuacao = (String) result[0];
            Long count = (Long) result[1];
            bolsistasPorArea.put(areaAtuacao, count);
        }

        return bolsistasPorArea;
    }

    // Listar todos os bolsistas como DTOs
    public List<BolsistaDTO> listarBolsistas() {
        List<Bolsista> bolsistas = bolsistaRepository.findAll();

        return bolsistas.stream().map(bolsista -> new BolsistaDTO(
            bolsista.getId(),
            bolsista.getNome(),
            bolsista.getAreaAtuacao(),
            bolsista.getProjeto() != null ? bolsista.getProjeto().getId() : null,
            bolsista.getConvenio(),
            bolsista.getCidade(),
            bolsista.getCpf(),
            bolsista.getTelefone(),
            bolsista.getValorBolsa(),
            bolsista.getDuracaoBolsa()
        )).collect(Collectors.toList());
    }
}
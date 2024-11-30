package com.example.api2024.service;

import com.example.api2024.entity.Historico;
import com.example.api2024.repository.HistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoricoService {

    @Autowired
    private HistoricoRepository historicoRepository;

    public Historico cadastrarHistorico(Long admAlterador, String alteracao, String alterado, Long idAlterado, String dados,  String arquivos) {
        Historico historico = new Historico();
        historico.setAdmAlterador(admAlterador);
        historico.setAlteracao(alteracao);
        historico.setAlterado(alterado);
        historico.setIdAlterado(idAlterado);
        historico.setDados(dados);
        historico.setArquivos(arquivos);
        return historicoRepository.save(historico);
    }

    public Historico buscarUltimoHistoricoPorIdAlterado(Long idAlterado, String alterado) {
        return historicoRepository.findHistoricosByIdAlterado(idAlterado, alterado)
                .stream()
                .findFirst()
                .orElse(null);
    }

    public List<Historico> listarHistorico() {
        return historicoRepository.findAll();
    }

    public Historico buscarHistoricoPorId(Long id) {
        return historicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Historico não encontrado."));
    }
}
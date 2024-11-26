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

    public Historico cadastrarHistorico(Long admAlterador, String alteracao, String alterado, Long idAlterado, String dadosAntigos, String dadosNovos) {
        Historico historico = new Historico();
        historico.setAdmAlterador(admAlterador);
        historico.setAlteracao(alteracao);
        historico.setAlterado(alterado);
        historico.setIdAlterado(idAlterado);
        historico.setDadosAntigos(dadosAntigos);
        historico.setDadosNovos(dadosNovos);
        return historicoRepository.save(historico);
    }

    public List<Historico> listarHistorico() {
        return historicoRepository.findAll();
    }
}
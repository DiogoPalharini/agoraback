package com.example.api2024.service;

import com.example.api2024.entity.AnaliseProjeto;
import com.example.api2024.entity.Projeto;
import com.example.api2024.repository.AnaliseProjetoRepository;
import com.example.api2024.repository.ProjetoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnaliseProjetoService {
	
	@Autowired
	private ProjetoRepository projetoRepository;

    @Autowired
    private AnaliseProjetoRepository analiseProjetoRepository;

    public AnaliseProjeto salvarAnalise(AnaliseProjeto analiseProjeto, String idProjeto) {
        // Busca o projeto pelo ID
        Projeto projeto = projetoRepository.findByReferenciaProjeto(idProjeto);

        // Associa o projeto à análise
        analiseProjeto.setProjeto(projeto);

        // Salva a análise
        return analiseProjetoRepository.save(analiseProjeto);
    }

    public List<AnaliseProjeto> listarTodas() {
        return analiseProjetoRepository.findAll();
    }

    public AnaliseProjeto buscarPorId(Long id) {
        return analiseProjetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Análise não encontrada com ID: " + id));
    }

    public AnaliseProjeto atualizarAnalise(Long id, AnaliseProjeto analiseAtualizada) {
        AnaliseProjeto analiseExistente = buscarPorId(id);
        analiseExistente.setValorGasto(analiseAtualizada.getValorGasto());
        analiseExistente.setAutor(analiseAtualizada.getAutor());
        analiseExistente.setInformacoesAdicionais(analiseAtualizada.getInformacoesAdicionais());
        analiseExistente.setResultadoObtido(analiseAtualizada.getResultadoObtido());
        analiseExistente.setProjeto(analiseAtualizada.getProjeto());
        return analiseProjetoRepository.save(analiseExistente);
    }
    
    public AnaliseProjeto salvar(AnaliseProjeto analiseProjeto) {
        return analiseProjetoRepository.save(analiseProjeto);
    }

    public void excluirAnalise(Long id) {
        if (!analiseProjetoRepository.existsById(id)) {
            throw new RuntimeException("Análise não encontrada com ID: " + id);
        }
        analiseProjetoRepository.deleteById(id);
    }
}
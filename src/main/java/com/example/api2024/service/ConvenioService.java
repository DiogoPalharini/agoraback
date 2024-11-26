package com.example.api2024.service;

import com.example.api2024.entity.Convenio;
import com.example.api2024.repository.ConvenioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ConvenioService {

    @Autowired
    private ConvenioRepository convenioRepository;

    // Create
    public Convenio criarConvenio(Convenio convenio) {
        return convenioRepository.save(convenio);
    }

    // Read All
    public List<Convenio> listarConvenios() {
        return convenioRepository.findAll();
    }
    
    // Read por ID
    public Convenio buscarConvenioPorId(Long id) {
        return convenioRepository.findById(id).orElse(null);
    }

    // Update
    public Convenio atualizarConvenio(Long id, Convenio convenioAtualizado) {
        Optional<Convenio> convenioExistente = convenioRepository.findById(id);
        if (convenioExistente.isPresent()) {
            Convenio convenio = convenioExistente.get();
            convenio.setNome(convenioAtualizado.getNome());
            convenio.setTipoConvenio(convenioAtualizado.getTipoConvenio());
            convenio.setObjetivo(convenioAtualizado.getObjetivo());
            convenio.setInstituicao(convenioAtualizado.getInstituicao());
            convenio.setPrazo(convenioAtualizado.getPrazo());
            return convenioRepository.save(convenio);
        }
        return null;
    }

    // Delete
    public boolean deletarConvenio(Long id) {
        if (convenioRepository.existsById(id)) {
            convenioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Map<String, Long> getInstituicoesPorTipoConvenio() {
        List<Object[]> resultados = convenioRepository.countInstituicoesByTipoConvenio();
        Map<String, Long> instituicoesPorConvenio = new HashMap<>();
    
        for (Object[] resultado : resultados) {
            String tipoConvenio = (String) resultado[0];
            Long count = (Long) resultado[1];
            instituicoesPorConvenio.put(tipoConvenio, count);
        }
    
        return instituicoesPorConvenio;
    }    
}
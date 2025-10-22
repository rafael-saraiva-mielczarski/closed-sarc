package com.closed_sarc.app_reservation_api.service;

import com.closed_sarc.app_reservation_api.domain.entities.Recurso;
import com.closed_sarc.app_reservation_api.domain.repositories.RecursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RecursoService {
    private final RecursoRepository recursoRepository;

    public Recurso create(Recurso recurso) {
        // Verificar se já existe um recurso com o mesmo nome
        if (recursoRepository.findByNomeIgnoreCaseAndAtivoTrue(recurso.getNome()).isPresent()) {
            throw new IllegalArgumentException("Já existe um recurso ativo com este nome");
        }
        
        return recursoRepository.save(recurso);
    }

    @Transactional(readOnly = true)
    public List<Recurso> findAll() {
        return recursoRepository.findAll().stream()
                .filter(Recurso::getAtivo)
                .toList();
    }

    public void delete(UUID id) {
        Recurso recurso = recursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recurso não encontrado"));
        recurso.setAtivo(false);
        recursoRepository.save(recurso);
    }
}

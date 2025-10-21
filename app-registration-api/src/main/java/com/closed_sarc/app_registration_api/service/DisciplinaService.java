package com.closed_sarc.app_registration_api.service;

import com.closed_sarc.app_registration_api.domain.entities.Disciplina;
import com.closed_sarc.app_registration_api.domain.repositories.DisciplinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DisciplinaService {
    private final DisciplinaRepository disciplinaRepository;

    public Disciplina create(Disciplina disciplina) {
        // Verificar se já existe uma disciplina com o mesmo nome
        if (disciplinaRepository.findByNomeIgnoreCase(disciplina.getNome()).isPresent()) {
            throw new IllegalArgumentException("Já existe uma disciplina com este nome");
        }
        
        return disciplinaRepository.save(disciplina);
    }

    @Transactional(readOnly = true)
    public Disciplina findById(UUID id) {
        return disciplinaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Disciplina não encontrada"));
    }

    @Transactional(readOnly = true)
    public List<Disciplina> findAll() {
        return disciplinaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Disciplina> findByNomeContaining(String nome) {
        return disciplinaRepository.findByNomeContainingIgnoreCase(nome);
    }

}

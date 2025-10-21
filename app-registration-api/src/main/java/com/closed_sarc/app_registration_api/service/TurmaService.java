package com.closed_sarc.app_registration_api.service;

import com.closed_sarc.app_registration_api.domain.entities.Turma;
import com.closed_sarc.app_registration_api.domain.entities.Usuario;
import com.closed_sarc.app_registration_api.domain.entities.Disciplina;
import com.closed_sarc.app_registration_api.domain.entities.TipoUsuario;
import com.closed_sarc.app_registration_api.domain.repositories.TurmaRepository;
import com.closed_sarc.app_registration_api.domain.repositories.UsuarioRepository;
import com.closed_sarc.app_registration_api.domain.repositories.DisciplinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TurmaService {
    private final TurmaRepository turmaRepository;
    private final UsuarioRepository usuarioRepository;
    private final DisciplinaRepository disciplinaRepository;

    public Turma create(Turma turma, UUID professorId, UUID disciplinaId) {
        // Validar se o professor existe e é do tipo PROFESSOR
        Usuario professor = usuarioRepository.findById(professorId)
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado"));
        
        if (professor.getTipo() != TipoUsuario.PROFESSOR) {
            throw new IllegalArgumentException("Usuário informado não é um professor");
        }

        // Validar se a disciplina existe
        Disciplina disciplina = disciplinaRepository.findById(disciplinaId)
                .orElseThrow(() -> new IllegalArgumentException("Disciplina não encontrada"));

        // Definir as relações
        turma.setProfessor(professor);
        turma.setDisciplina(disciplina);

        return turmaRepository.save(turma);
    }

    @Transactional(readOnly = true)
    public Turma findById(UUID id) {
        return turmaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada"));
    }

    @Transactional(readOnly = true)
    public List<Turma> findAll() {
        return turmaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Turma> findByProfessorId(UUID professorId) {
        return turmaRepository.findByProfessorId(professorId);
    }

    @Transactional(readOnly = true)
    public List<Turma> findByDisciplinaId(UUID disciplinaId) {
        return turmaRepository.findByDisciplinaId(disciplinaId);
    }

}

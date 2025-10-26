package com.closed_sarc.app_registration_api.web;

import com.closed_sarc.app_registration_api.domain.entities.*;
import com.closed_sarc.app_registration_api.service.TurmaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/turmas")
@RequiredArgsConstructor
public class TurmaController {
    private final TurmaService turmaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody TurmaRequest turmaRequest) {
        try {
            Turma turma = Turma.builder()
                    .nome(turmaRequest.nome())
                    .semestre(turmaRequest.semestre())
                    .ano(turmaRequest.ano())
                    .horario(turmaRequest.horario())
                    .diasAula(turmaRequest.diasAula())
                    .build();

            Turma saved = turmaService.create(turma, turmaRequest.professorId(), turmaRequest.disciplinaId());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        try {
            Turma turma = turmaService.findById(id);
            return ResponseEntity.ok(turma);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<List<Turma>> getAll() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuarioLogado = (Usuario) auth.getPrincipal();
            
            List<Turma> turmas;
            
            if (usuarioLogado.getTipo() == TipoUsuario.ADMIN) {
                turmas = turmaService.findAll();
            } else if (usuarioLogado.getTipo() == TipoUsuario.PROFESSOR) {
                turmas = turmaService.findByProfessorId(usuarioLogado.getId());
            } else {
                turmas = List.of(); // Para outros tipos de usuário, retorna vazio
            }
            
            return ResponseEntity.ok(turmas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/professor/{professorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<List<Turma>> getByProfessor(@PathVariable UUID professorId) {
        try {
            List<Turma> turmas = turmaService.findByProfessorId(professorId);
            return ResponseEntity.ok(turmas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/disciplina/{disciplinaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<List<Turma>> getByDisciplina(@PathVariable UUID disciplinaId) {
        try {
            List<Turma> turmas = turmaService.findByDisciplinaId(disciplinaId);
            return ResponseEntity.ok(turmas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // Record para o request
    public record TurmaRequest(
            String nome,
            Semestre semestre,
            Integer ano,
            Horario horario,
            List<DiaSemana> diasAula,
            UUID professorId,
            UUID disciplinaId
    ) {}

    // Record para resposta de erro
    public record ErrorResponse(String message) {}
}

package com.closed_sarc.app_registration_api.web;

import com.closed_sarc.app_registration_api.domain.entities.Disciplina;
import com.closed_sarc.app_registration_api.service.DisciplinaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/disciplinas")
@RequiredArgsConstructor
public class DisciplinaController {
    private final DisciplinaService disciplinaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody DisciplinaRequest disciplinaRequest) {
        try {
            Disciplina disciplina = Disciplina.builder()
                    .nome(disciplinaRequest.nome())
                    .descricao(disciplinaRequest.descricao())
                    .cargaHoraria(disciplinaRequest.cargaHoraria())
                    .build();

            Disciplina saved = disciplinaService.create(disciplina);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'ESTUDANTE')")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        try {
            Disciplina disciplina = disciplinaService.findById(id);
            return ResponseEntity.ok(disciplina);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'ESTUDANTE')")
    public ResponseEntity<List<Disciplina>> getAll() {
        try {
            List<Disciplina> disciplinas = disciplinaService.findAll();
            return ResponseEntity.ok(disciplinas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/nome/{nome}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'ESTUDANTE')")
    public ResponseEntity<List<Disciplina>> getByNome(@PathVariable String nome) {
        try {
            List<Disciplina> disciplinas = disciplinaService.findByNomeContaining(nome);
            return ResponseEntity.ok(disciplinas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // Record para o request
    public record DisciplinaRequest(
            String nome,
            String descricao,
            Integer cargaHoraria
    ) {}

    // Record para resposta de erro
    public record ErrorResponse(String message) {}
}

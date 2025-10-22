package com.closed_sarc.app_registration_api.web;

import com.closed_sarc.app_registration_api.application.dto.RecursoDTO;
import com.closed_sarc.app_registration_api.application.dto.RecursoRequestDTO;
import com.closed_sarc.app_registration_api.service.RecursoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recursos")
@RequiredArgsConstructor
public class RecursoController {
    private final RecursoService recursoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody RecursoRequestDTO recursoRequest) {
        try {
            RecursoDTO saved = recursoService.create(recursoRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'ESTUDANTE')")
    public ResponseEntity<List<RecursoDTO>> getAll() {
        try {
            List<RecursoDTO> recursos = recursoService.findAll();
            return ResponseEntity.ok(recursos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'ESTUDANTE')")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        try {
            RecursoDTO recurso = recursoService.findById(id);
            return ResponseEntity.ok(recurso);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            recursoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }

    // Record para resposta de erro
    public record ErrorResponse(String message) {}
}

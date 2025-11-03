package com.closed_sarc.app_reservation_api.web;

import com.closed_sarc.app_reservation_api.domain.entities.Recurso;
import com.closed_sarc.app_reservation_api.service.RecursoService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recursos")
@RequiredArgsConstructor
@Tag(name = "Recursos", description = "Endpoints para gerenciamento de recursos.")
public class RecursoController {
    private final RecursoService recursoService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody RecursoRequest recursoRequest) {
        try {
            Recurso recurso = Recurso.builder()
                    .nome(recursoRequest.nome())
                    .tipo(recursoRequest.tipo())
                    .quantidade(recursoRequest.quantidade())
                    .capacidade(recursoRequest.capacidade())
                    .ativo(true)
                    .build();

            Recurso saved = recursoService.create(recurso);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }

    @GetMapping
    public ResponseEntity<List<Recurso>> getAll() {
        try {
            List<Recurso> recursos = recursoService.findAll();
            return ResponseEntity.ok(recursos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            recursoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }

    // Record para o request
    public record RecursoRequest(
            String nome,
            String tipo,
            Integer quantidade,
            Integer capacidade
    ) {}

    // Record para resposta de erro
    public record ErrorResponse(String message) {}
}

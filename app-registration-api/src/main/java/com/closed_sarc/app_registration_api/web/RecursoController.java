package com.closed_sarc.app_registration_api.web;

import com.closed_sarc.app_registration_api.application.dto.RecursoDTO;
import com.closed_sarc.app_registration_api.application.dto.RecursoRequestDTO;
import com.closed_sarc.app_registration_api.service.RecursoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Recursos", description = "API para gerenciamento de recursos (salas, equipamentos, etc.)")
public class RecursoController {
    private final RecursoService recursoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar novo recurso", 
            description = "Cria um novo recurso no sistema. Recursos podem ser salas, equipamentos, etc. Cada recurso possui uma quantidade disponível.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecursoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou recurso já existe",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Já existe um recurso ativo com este nome\"}"))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão - apenas ADMIN pode criar recursos")
    })
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
    @Operation(summary = "Listar todos os recursos", 
            description = "Retorna a lista de todos os recursos ativos disponíveis no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de recursos retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecursoDTO.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
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
    @Operation(summary = "Buscar recurso por ID", 
            description = "Retorna os detalhes de um recurso específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recurso encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecursoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<?> getById(
            @Parameter(description = "ID do recurso", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
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
    @Operation(summary = "Excluir recurso", 
            description = "Remove um recurso do sistema (desativa logicamente). Apenas ADMIN pode excluir recursos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão - apenas ADMIN pode excluir recursos")
    })
    public ResponseEntity<?> delete(
            @Parameter(description = "ID do recurso a ser excluído", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
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

    @Schema(description = "DTO de resposta de erro")
    public record ErrorResponse(
            @Schema(description = "Mensagem de erro", example = "Recurso não encontrado")
            String message
    ) {}
}

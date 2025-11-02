package com.closed_sarc.app_registration_api.web;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.closed_sarc.app_registration_api.domain.entities.Evento;
import com.closed_sarc.app_registration_api.service.EventoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
@Tag(name = "Eventos", description = "API para gerenciamento de eventos")
public class EventoController {
    
    private final EventoService eventoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    @Operation(summary = "Criar novo evento", 
            description = "Cria um novo evento no sistema. ADMIN e PROFESSOR podem criar eventos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Evento criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Evento.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão - apenas ADMIN ou PROFESSOR podem criar eventos")
    })
    public ResponseEntity<?> create(@RequestBody Evento evento) {
        try {
            Evento saved = eventoService.create(evento);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('ESTUDANTE')")
    @Operation(summary = "Listar todos os eventos", 
            description = "Retorna a lista de todos os eventos do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de eventos retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Evento.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<List<Evento>> getAll() {
        try {
            List<Evento> eventos = eventoService.findAll();
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('ESTUDANTE')")
    @Operation(summary = "Buscar evento por ID", 
            description = "Retorna os detalhes de um evento específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Evento.class))),
            @ApiResponse(responseCode = "400", description = "Evento não encontrado",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<?> getById(
            @Parameter(description = "ID do evento", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        try {
            Evento evento = eventoService.findById(id);
            return ResponseEntity.ok(evento);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or (hasRole('ESTUDANTE') and #usuarioId == authentication.principal.id)")
    @Operation(summary = "Buscar eventos por usuário", 
            description = "Retorna todos os eventos associados a um usuário específico. Estudantes só podem ver seus próprios eventos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de eventos do usuário retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Evento.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<List<Evento>> getByUsuarioId(
            @Parameter(description = "ID do usuário", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID usuarioId) {
        try {
            List<Evento> eventos = eventoService.findByUsuarioId(usuarioId);
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir evento", 
            description = "Remove um evento do sistema. Apenas ADMIN pode excluir eventos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Evento excluído com sucesso"),
            @ApiResponse(responseCode = "400", description = "Evento não encontrado",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão - apenas ADMIN pode excluir eventos")
    })
    public ResponseEntity<?> delete(
            @Parameter(description = "ID do evento a ser excluído", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        try {
            eventoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @Schema(description = "DTO de resposta de erro")
    public record ErrorResponse(
            @Schema(description = "Mensagem de erro", example = "Evento não encontrado")
            String message
    ) {}
}
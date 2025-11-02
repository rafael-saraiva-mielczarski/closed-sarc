package com.closed_sarc.app_registration_api.web;

import com.closed_sarc.app_registration_api.domain.entities.*;
import com.closed_sarc.app_registration_api.service.TurmaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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
@RequestMapping("/api/turmas")
@RequiredArgsConstructor
@Tag(name = "Turmas", description = "API para gerenciamento de turmas")
public class TurmaController {
    private final TurmaService turmaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar nova turma", 
            description = "Cria uma nova turma no sistema. É necessário informar o ID do professor e da disciplina.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Turma criada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Turma.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão - apenas ADMIN pode criar turmas")
    })
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
    @Operation(summary = "Buscar turma por ID", 
            description = "Retorna os detalhes de uma turma específica pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turma encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Turma.class))),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<?> getById(
            @Parameter(description = "ID da turma", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
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
    @Operation(summary = "Listar todas as turmas", 
            description = "Retorna a lista de todas as turmas do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de turmas retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Turma.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<List<Turma>> getAll() {
        try {
            List<Turma> turmas = turmaService.findAll();
            return ResponseEntity.ok(turmas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/professor/{professorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(summary = "Buscar turmas por professor", 
            description = "Retorna todas as turmas associadas a um professor específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de turmas do professor retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Turma.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<List<Turma>> getByProfessor(
            @Parameter(description = "ID do professor", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID professorId) {
        try {
            List<Turma> turmas = turmaService.findByProfessorId(professorId);
            return ResponseEntity.ok(turmas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/disciplina/{disciplinaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(summary = "Buscar turmas por disciplina", 
            description = "Retorna todas as turmas associadas a uma disciplina específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de turmas da disciplina retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Turma.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<List<Turma>> getByDisciplina(
            @Parameter(description = "ID da disciplina", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID disciplinaId) {
        try {
            List<Turma> turmas = turmaService.findByDisciplinaId(disciplinaId);
            return ResponseEntity.ok(turmas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @Schema(description = "DTO para requisição de criação de turma")
    public record TurmaRequest(
            @Schema(description = "Nome da turma", example = "33A", required = true)
            String nome,
            
            @Schema(description = "Semestre da turma", example = "PRIMEIRO", required = true)
            Semestre semestre,
            
            @Schema(description = "Ano da turma", example = "2025", required = true)
            Integer ano,
            
            @Schema(description = "Horário da turma", example = "A", required = true)
            Horario horario,
            
            @Schema(description = "Dias da semana que a turma tem aula", example = "[\"SEGUNDA\", \"QUARTA\"]", required = true)
            List<DiaSemana> diasAula,
            
            @Schema(description = "ID do professor responsável", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            UUID professorId,
            
            @Schema(description = "ID da disciplina", example = "660e8400-e29b-41d4-a716-446655440001", required = true)
            UUID disciplinaId
    ) {}

    @Schema(description = "DTO de resposta de erro")
    public record ErrorResponse(
            @Schema(description = "Mensagem de erro", example = "Turma não encontrada")
            String message
    ) {}
}

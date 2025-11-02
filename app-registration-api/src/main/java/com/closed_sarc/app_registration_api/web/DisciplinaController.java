package com.closed_sarc.app_registration_api.web;

import com.closed_sarc.app_registration_api.domain.entities.Disciplina;
import com.closed_sarc.app_registration_api.service.DisciplinaService;
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
@RequestMapping("/api/disciplinas")
@RequiredArgsConstructor
@Tag(name = "Disciplinas", description = "API para gerenciamento de disciplinas")
public class DisciplinaController {
    private final DisciplinaService disciplinaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar nova disciplina", 
            description = "Cria uma nova disciplina no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Disciplina criada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Disciplina.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão - apenas ADMIN pode criar disciplinas")
    })
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
    @Operation(summary = "Buscar disciplina por ID", 
            description = "Retorna os detalhes de uma disciplina específica pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disciplina encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Disciplina.class))),
            @ApiResponse(responseCode = "404", description = "Disciplina não encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<?> getById(
            @Parameter(description = "ID da disciplina", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
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
    @Operation(summary = "Listar todas as disciplinas", 
            description = "Retorna a lista de todas as disciplinas do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de disciplinas retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Disciplina.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
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
    @Operation(summary = "Buscar disciplinas por nome", 
            description = "Retorna todas as disciplinas cujo nome contém o termo pesquisado (busca parcial)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de disciplinas encontradas",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Disciplina.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<List<Disciplina>> getByNome(
            @Parameter(description = "Nome ou parte do nome da disciplina", example = "Programação")
            @PathVariable String nome) {
        try {
            List<Disciplina> disciplinas = disciplinaService.findByNomeContaining(nome);
            return ResponseEntity.ok(disciplinas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @Schema(description = "DTO para requisição de criação de disciplina")
    public record DisciplinaRequest(
            @Schema(description = "Nome da disciplina", example = "Programação Java", required = true)
            String nome,
            
            @Schema(description = "Descrição da disciplina", example = "Disciplina de programação orientada a objetos")
            String descricao,
            
            @Schema(description = "Carga horária em horas", example = "60")
            Integer cargaHoraria
    ) {}

    @Schema(description = "DTO de resposta de erro")
    public record ErrorResponse(
            @Schema(description = "Mensagem de erro", example = "Disciplina não encontrada")
            String message
    ) {}
}

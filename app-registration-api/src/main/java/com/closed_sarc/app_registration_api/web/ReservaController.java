package com.closed_sarc.app_registration_api.web;

import com.closed_sarc.app_registration_api.application.service.ReservaService;
import com.closed_sarc.app_registration_api.domain.entities.Horario;
import com.closed_sarc.app_registration_api.infrastructure.client.dto.ReservaResponseDTO;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "API para gerenciamento de reservas de recursos para turmas")
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    @Operation(summary = "Reservar recurso para uma turma", 
            description = "Cria uma reserva de recurso para uma turma. Valida se a turma tem aula no dia/horário especificado baseado no ano, semestre e dias da semana da turma. O dia da semana é extraído automaticamente da data informada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reserva criada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou turma não tem aula na data/horário especificado",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"A data 2025-01-21 não é uma data de aula válida para a turma 33A no semestre PRIMEIRO/2025\"}"))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> reservarRecurso(@RequestBody ReservaRequest request) {
        try {
            ReservaResponseDTO reserva = reservaService.reservarRecursoParaTurma(
                    request.turmaId(),
                    request.recursoId(),
                    request.quantidade(),
                    request.data(),
                    request.horario()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor: " + e.getMessage()));
        }
    }

    @GetMapping("/turma/{turmaId}")
    @Operation(summary = "Listar reservas de uma turma", 
            description = "Retorna todas as reservas de recursos para uma turma específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<ReservaResponseDTO>> listarReservasPorTurma(
            @Parameter(description = "ID da turma", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID turmaId) {
        try {
            List<ReservaResponseDTO> reservas = reservaService.buscarReservasPorTurma(turmaId);
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/turma/{turmaId}/data")
    @Operation(summary = "Listar reservas de uma turma por data e horário", 
            description = "Retorna as reservas de recursos para uma turma em uma data e horário específicos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<ReservaResponseDTO>> listarReservasPorTurmaEData(
            @Parameter(description = "ID da turma", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID turmaId,
            @Parameter(description = "Data da reserva (formato: YYYY-MM-DD)", example = "2025-01-20")
            @RequestParam("data") LocalDate data,
            @Parameter(description = "Horário da reserva (A, B, C, D, E, etc.)", example = "A")
            @RequestParam("horario") Horario horario) {
        try {
            List<ReservaResponseDTO> reservas = reservaService.buscarReservasPorTurmaEData(turmaId, data, horario);
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Schema(description = "DTO para requisição de criação de reserva")
    public record ReservaRequest(
            @Schema(description = "ID da turma", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            UUID turmaId,
            
            @Schema(description = "ID do recurso a ser reservado", example = "660e8400-e29b-41d4-a716-446655440001", required = true)
            UUID recursoId,
            
            @Schema(description = "Quantidade a ser reservada", example = "1", required = true)
            Integer quantidade,
            
            @Schema(description = "Data da reserva (formato: YYYY-MM-DD)", example = "2025-01-20", required = true)
            LocalDate data,
            
            @Schema(description = "Horário da reserva (A, B, C, D, E, etc.)", example = "A", required = true)
            Horario horario
    ) {}

    @Schema(description = "DTO de resposta de erro")
    public record ErrorResponse(
            @Schema(description = "Mensagem de erro", example = "Turma não encontrada")
            String message
    ) {}
}


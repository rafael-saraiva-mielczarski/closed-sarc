package com.closed_sarc.app_registration_api.web;

import com.closed_sarc.app_registration_api.application.service.ReservaService;
import com.closed_sarc.app_registration_api.domain.entities.Horario;
import com.closed_sarc.app_registration_api.infrastructure.client.dto.ReservaResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
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
            description = "Cria uma reserva de recurso para uma turma. Valida se a turma tem aula no dia/horário especificado. O dia da semana é extraído automaticamente da data informada.")
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
    public ResponseEntity<List<ReservaResponseDTO>> listarReservasPorTurma(@PathVariable UUID turmaId) {
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
    public ResponseEntity<List<ReservaResponseDTO>> listarReservasPorTurmaEData(
            @PathVariable UUID turmaId,
            @RequestParam("data") LocalDate data,
            @RequestParam("horario") Horario horario) {
        try {
            List<ReservaResponseDTO> reservas = reservaService.buscarReservasPorTurmaEData(turmaId, data, horario);
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public record ReservaRequest(
            UUID turmaId,
            UUID recursoId,
            Integer quantidade,
            LocalDate data,
            Horario horario
    ) {}

    public record ErrorResponse(String message) {}
}


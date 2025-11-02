package com.closed_sarc.app_reservation_api.web;

import com.closed_sarc.app_reservation_api.domain.entities.Reserva;
import com.closed_sarc.app_reservation_api.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody ReservationRequest request) {
        try {
            Reserva reserva = reservationService.createReservation(
                    request.turmaId(),
                    request.recursoId(),
                    request.quantidade(),
                    request.dataUso()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }

    @GetMapping("/turma/{turmaId}")
    public ResponseEntity<List<Reserva>> getReservationsByTurma(@PathVariable UUID turmaId) {
        try {
            List<Reserva> reservas = reservationService.findReservationsByTurmaId(turmaId);
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/turma/{turmaId}/data")
    public ResponseEntity<List<Reserva>> getReservationsByTurmaAndData(
            @PathVariable UUID turmaId,
            @RequestParam("dataUso") Instant dataUso) {
        try {
            List<Reserva> reservas = reservationService.findReservationsByTurmaAndDataUso(turmaId, dataUso);
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Record para o request
    public record ReservationRequest(
            UUID turmaId,
            UUID recursoId,
            Integer quantidade,
            Instant dataUso
    ) {}

    // Record para resposta de erro
    public record ErrorResponse(String message) {}
}


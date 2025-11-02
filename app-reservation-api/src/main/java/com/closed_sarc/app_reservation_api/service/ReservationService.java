package com.closed_sarc.app_reservation_api.service;

import com.closed_sarc.app_reservation_api.domain.entities.Recurso;
import com.closed_sarc.app_reservation_api.domain.entities.Reserva;
import com.closed_sarc.app_reservation_api.domain.repositories.RecursoRepository;
import com.closed_sarc.app_reservation_api.domain.repositories.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {
    private final ReservaRepository reservaRepository;
    private final RecursoRepository recursoRepository;

    public Reserva createReservation(UUID turmaId, UUID recursoId, Integer quantidade, Instant dataUso) {
        // Verificar se o recurso existe e está ativo
        Recurso recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new IllegalArgumentException("Recurso não encontrado"));

        if (!recurso.getAtivo()) {
            throw new IllegalArgumentException("Recurso não está ativo");
        }

        // Verificar disponibilidade do recurso na data/horário
        int quantidadeReservada = getQuantidadeReservadaPorDataUso(recursoId, dataUso);
        int quantidadeDisponivel = (recurso.getQuantidade() != null ? recurso.getQuantidade() : 1) - quantidadeReservada;

        if (quantidade > quantidadeDisponivel) {
            throw new IllegalArgumentException(
                    String.format("Quantidade solicitada (%d) excede a disponibilidade (%d) do recurso %s",
                            quantidade, quantidadeDisponivel, recurso.getNome()));
        }

        // Criar reserva
        Reserva reserva = Reserva.builder()
                .turmaId(turmaId)
                .recurso(recurso)
                .quantidade(quantidade)
                .dataReserva(Instant.now())
                .dataUso(dataUso)
                .build();

        return reservaRepository.save(reserva);
    }

    @Transactional(readOnly = true)
    public List<Reserva> findReservationsByTurmaAndDataUso(UUID turmaId, Instant dataUso) {
        return reservaRepository.findByTurmaIdAndDataUso(turmaId, dataUso);
    }

    @Transactional(readOnly = true)
    public List<Reserva> findReservationsByTurmaId(UUID turmaId) {
        return reservaRepository.findByTurmaId(turmaId);
    }

    @Transactional(readOnly = true)
    private int getQuantidadeReservadaPorDataUso(UUID recursoId, Instant dataUso) {
        return reservaRepository.findByRecursoIdAndDataUso(recursoId, dataUso)
                .stream()
                .mapToInt(Reserva::getQuantidade)
                .sum();
    }
}


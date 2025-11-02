package com.closed_sarc.app_registration_api.application.service;

import com.closed_sarc.app_registration_api.domain.entities.DiaSemana;
import com.closed_sarc.app_registration_api.domain.entities.Horario;
import com.closed_sarc.app_registration_api.domain.entities.Semestre;
import com.closed_sarc.app_registration_api.domain.entities.Turma;
import com.closed_sarc.app_registration_api.domain.repositories.TurmaRepository;
import com.closed_sarc.app_registration_api.infrastructure.client.dto.ReservaResponseDTO;
import com.closed_sarc.app_registration_api.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservaService {

    private final TurmaRepository turmaRepository;
    private final ReservationService reservationService;

    @Transactional(readOnly = true)
    public ReservaResponseDTO reservarRecursoParaTurma(UUID turmaId, UUID recursoId, Integer quantidade, LocalDate data, Horario horario) {
        // Verificar se a turma existe
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada"));

        // Validar que o horário da reserva corresponde ao horário da turma
        if (!turma.getHorario().equals(horario)) {
            throw new IllegalArgumentException(
                    String.format("O horário da reserva (%s) não corresponde ao horário da turma (%s)",
                            horario, turma.getHorario()));
        }

        // Gerar lista dinâmica de datas de aula da turma
        List<LocalDate> datasAula = gerarDatasAulaTurma(turma);

        // Validar se a data solicitada está na lista de datas de aula
        if (!datasAula.contains(data)) {
            throw new IllegalArgumentException(
                    String.format("A data %s não é uma data de aula válida para a turma %s no semestre %s/%d",
                            data, turma.getNome(), turma.getSemestre(), turma.getAno()));
        }

        // Converter data e horário para Instant (dataUso)
        Instant dataUso = converterParaInstant(data, horario);

        // Fazer a reserva na reservation-api
        return reservationService.createReservation(turmaId, recursoId, quantidade, dataUso);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> buscarReservasPorTurmaEData(UUID turmaId, LocalDate data, Horario horario) {
        Instant dataUso = converterParaInstant(data, horario);
        return reservationService.getReservationsByTurmaAndData(turmaId, dataUso);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> buscarReservasPorTurma(UUID turmaId) {
        return reservationService.getReservationsByTurma(turmaId);
    }

    /**
     * Converte LocalDate e Horario para Instant
     * O horário é convertido baseado no enum Horario (A, B, C, etc.)
     * Assumindo que cada letra representa um período de aula (ex: A = 08:00-09:00)
     */
    private Instant converterParaInstant(LocalDate data, Horario horario) {
        LocalTime horaInicio = obterHoraInicioPorHorario(horario);
        return data.atTime(horaInicio)
                .atZone(ZoneId.of("UTC"))
                .toInstant();
    }

    /**
     * Converte o enum Horario para LocalTime
     * Mapeamento básico: A=08:00, B=09:00, C=10:00, etc.
     * Pode ser ajustado conforme necessário
     */
    private LocalTime obterHoraInicioPorHorario(Horario horario) {
        int horaBase = 8; // Começa às 8h
        int indice = horario.ordinal();
        return LocalTime.of(horaBase + indice, 0);
    }

    /**
     * Gera dinamicamente a lista de datas de aula da turma baseado em:
     * - Ano
     * - Semestre (PRIMEIRO: janeiro a junho, SEGUNDO: julho a dezembro)
     * - Dias da semana que a turma tem aula
     */
    private List<LocalDate> gerarDatasAulaTurma(Turma turma) {
        List<LocalDate> datasAula = new ArrayList<>();
        
        int ano = turma.getAno();
        Semestre semestre = turma.getSemestre();
        List<DiaSemana> diasAula = turma.getDiasAula();
        
        // Determinar o período do semestre
        LocalDate dataInicio;
        LocalDate dataFim;
        
        if (semestre == Semestre.PRIMEIRO) {
            // Primeiro semestre: janeiro a junho
            dataInicio = LocalDate.of(ano, 1, 1);
            dataFim = LocalDate.of(ano, 6, 30);
        } else {
            // Segundo semestre: julho a dezembro
            dataInicio = LocalDate.of(ano, 7, 1);
            dataFim = LocalDate.of(ano, 12, 31);
        }
        
        // Converter DiasSemana para DayOfWeek
        List<DayOfWeek> diasSemanaJava = diasAula.stream()
                .map(this::converterParaDayOfWeek)
                .collect(Collectors.toList());
        
        // Gerar todas as datas dos dias da semana especificados no período
        LocalDate dataAtual = dataInicio;
        while (!dataAtual.isAfter(dataFim)) {
            if (diasSemanaJava.contains(dataAtual.getDayOfWeek())) {
                datasAula.add(dataAtual);
            }
            dataAtual = dataAtual.plusDays(1);
        }
        
        log.debug("Geradas {} datas de aula para turma {} no semestre {}/{}", 
                datasAula.size(), turma.getNome(), semestre, ano);
        
        return datasAula;
    }

    /**
     * Converte DiaSemana para DayOfWeek
     */
    private DayOfWeek converterParaDayOfWeek(DiaSemana diaSemana) {
        return switch (diaSemana) {
            case SEGUNDA -> DayOfWeek.MONDAY;
            case TERCA -> DayOfWeek.TUESDAY;
            case QUARTA -> DayOfWeek.WEDNESDAY;
            case QUINTA -> DayOfWeek.THURSDAY;
            case SEXTA -> DayOfWeek.FRIDAY;
            case SABADO -> DayOfWeek.SATURDAY;
            case DOMINGO -> DayOfWeek.SUNDAY;
        };
    }
}


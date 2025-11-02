package com.closed_sarc.app_registration_api.application.service.impl;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.closed_sarc.app_registration_api.application.dto.AulaDTO;
import com.closed_sarc.app_registration_api.application.dto.CronogramaDTO;
import com.closed_sarc.app_registration_api.application.dto.EventoDTO;
import com.closed_sarc.app_registration_api.application.dto.RecursoReservadoDTO;
import com.closed_sarc.app_registration_api.application.service.CronogramaService;
import com.closed_sarc.app_registration_api.domain.entities.DiaSemana;
import com.closed_sarc.app_registration_api.domain.entities.Evento;
import com.closed_sarc.app_registration_api.domain.entities.Turma;
import com.closed_sarc.app_registration_api.domain.repositories.EventoRepository;
import com.closed_sarc.app_registration_api.domain.repositories.TurmaRepository;
import com.closed_sarc.app_registration_api.infrastructure.client.dto.ReservaResponseDTO;
import com.closed_sarc.app_registration_api.service.ReservationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CronogramaServiceImpl implements CronogramaService {

  private final TurmaRepository turmaRepository;
  private final EventoRepository eventoRepository;
  private final ReservationService reservationService;

  @Override
  public CronogramaDTO consultarCronograma() {
    DiaSemana hoje = getDiaSemanaAtual();

    List<Turma> turmasDeHoje = turmaRepository.findByDiasAulaContaining(hoje);

    List<AulaDTO> aulas = turmasDeHoje.isEmpty() 
        ? Collections.emptyList()
        : turmasDeHoje.stream()
            .map(this::convertToAulaDTO)
            .collect(Collectors.toList());

    // Buscar eventos do dia atual
    List<Evento> eventosDeHoje = eventoRepository.findByData(Instant.now());

    List<EventoDTO> eventos = eventosDeHoje.isEmpty()
        ? Collections.emptyList()
        : eventosDeHoje.stream()
            .map(this::convertToEventoDTO)
            .collect(Collectors.toList());

    return CronogramaDTO.builder()
        .aulasDeHoje(aulas)
        .eventosDeHoje(eventos)
        .build();
  }

  private AulaDTO convertToAulaDTO(Turma turma) {
    LocalDate hoje = LocalDate.now();
    
    // Buscar reservas para esta turma no dia/horário atual
    List<RecursoReservadoDTO> recursosReservados = Collections.emptyList();
    try {
      Instant dataUso = converterParaInstant(hoje, turma.getHorario());
      List<ReservaResponseDTO> reservas = reservationService.getReservationsByTurmaAndData(
          turma.getId(), dataUso);
      
      // Converter reservas para DTOs de recursos reservados
      recursosReservados = reservas.stream()
          .map(reserva -> RecursoReservadoDTO.builder()
              .recursoId(reserva.getRecurso().getId())
              .nomeRecurso(reserva.getRecurso().getNome())
              .tipoRecurso(reserva.getRecurso().getTipo())
              .quantidade(reserva.getQuantidade())
              .build())
          .collect(Collectors.toList());
    } catch (Exception e) {
      // Log do erro mas não interrompe a geração do cronograma
      // Se a reservation-api estiver indisponível, simplesmente não mostra as reservas
      System.err.println("Erro ao buscar reservas para turma " + turma.getId() + ": " + e.getMessage());
    }
    
    return AulaDTO.builder()
        .turmaId(turma.getId())
        .nomeProfessor(turma.getProfessor().getNome())
        .nomeDisciplina(turma.getDisciplina().getNome())
        .turma("(" + turma.getNome() + ")")
        .horario(turma.getHorario())
        .recursosReservados(recursosReservados)
        .build();
  }

  /**
   * Converte LocalDate e Horario para Instant
   */
  private Instant converterParaInstant(LocalDate data, com.closed_sarc.app_registration_api.domain.entities.Horario horario) {
    LocalTime horaInicio = obterHoraInicioPorHorario(horario);
    return data.atTime(horaInicio)
        .atZone(ZoneId.of("UTC"))
        .toInstant();
  }

  /**
   * Converte o enum Horario para LocalTime
   */
  private LocalTime obterHoraInicioPorHorario(com.closed_sarc.app_registration_api.domain.entities.Horario horario) {
    int horaBase = 8; // Começa às 8h
    int indice = horario.ordinal();
    return LocalTime.of(horaBase + indice, 0);
  }

  private EventoDTO convertToEventoDTO(Evento evento) {
    return EventoDTO.builder()
        .titulo(evento.getTitulo())
        .descricao(evento.getDescricao())
        .dataInicio(evento.getDataInicio())
        .dataFim(evento.getDataFim())
        .build();
  }

  private DiaSemana getDiaSemanaAtual() {
    DayOfWeek hoje = LocalDate.now().getDayOfWeek();
    return switch (hoje) {
      case MONDAY -> DiaSemana.SEGUNDA;
      case TUESDAY -> DiaSemana.TERCA;
      case WEDNESDAY -> DiaSemana.QUARTA;
      case THURSDAY -> DiaSemana.QUINTA;
      case FRIDAY -> DiaSemana.SEXTA;
      case SATURDAY -> DiaSemana.SABADO;
      case SUNDAY -> DiaSemana.DOMINGO;
    };
  }
}

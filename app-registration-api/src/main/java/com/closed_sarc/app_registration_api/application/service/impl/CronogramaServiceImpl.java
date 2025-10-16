package com.closed_sarc.app_registration_api.application.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.closed_sarc.app_registration_api.application.dto.AulaDTO;
import com.closed_sarc.app_registration_api.application.dto.CronogramaDTO;
import com.closed_sarc.app_registration_api.application.service.CronogramaService;
import com.closed_sarc.app_registration_api.domain.entities.DiaSemana;
import com.closed_sarc.app_registration_api.domain.entities.Turma;
import com.closed_sarc.app_registration_api.domain.repositories.TurmaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CronogramaServiceImpl implements CronogramaService {

  private final TurmaRepository turmaRepository;

  @Override
  public CronogramaDTO consultarCronograma() {
    DiaSemana hoje = getDiaSemanaAtual();

    List<Turma> turmasDeHoje = turmaRepository.findByDiasAulaContaining(hoje);

    if (turmasDeHoje.isEmpty()) {
      return CronogramaDTO.builder()
          .aulasDeHoje(Collections.emptyList())
          .build();
    }

    List<AulaDTO> aulas = turmasDeHoje.stream()
        .map(this::convertToAulaDTO)
        .collect(Collectors.toList());

    return CronogramaDTO.builder()
        .aulasDeHoje(aulas)
        .build();
  }

  private AulaDTO convertToAulaDTO(Turma turma) {
    String recurso = turma.getSala();
    return AulaDTO.builder()
        .recurso(recurso)
        .nomeProfessor(turma.getProfessor().getNome())
        .nomeDisciplina(turma.getDisciplina().getNome())
        .turma("(" + turma.getNome() + ")")
        .horario(turma.getHorario())
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

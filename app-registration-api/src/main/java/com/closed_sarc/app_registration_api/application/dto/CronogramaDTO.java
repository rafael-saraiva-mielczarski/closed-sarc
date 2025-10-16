package com.closed_sarc.app_registration_api.application.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Cronograma de aulas do dia")
public class CronogramaDTO {

  @Schema(description = "Lista de aulas programadas para hoje", example = "[{\"recurso\": \"401\", \"nomeProfessor\": \"Prof. Maria Silva\", \"nomeDisciplina\": \"Programação Java\", \"turma\": \"(33A)\", \"horario\": \"A\"}]")
  private List<AulaDTO> aulasDeHoje;
}

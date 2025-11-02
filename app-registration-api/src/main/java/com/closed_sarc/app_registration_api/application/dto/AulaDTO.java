package com.closed_sarc.app_registration_api.application.dto;

import com.closed_sarc.app_registration_api.domain.entities.Horario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informações de uma aula específica")
public class AulaDTO {

  @Schema(description = "ID da turma", example = "550e8400-e29b-41d4-a716-446655440000")
  private UUID turmaId;

  @Schema(description = "Nome do professor responsável pela disciplina", example = "Prof. Maria Silva")
  private String nomeProfessor;

  @Schema(description = "Nome da disciplina", example = "Programação Java")
  private String nomeDisciplina;

  @Schema(description = "Identificação da turma entre parênteses", example = "(33A)")
  private String turma;

  @Schema(description = "Horário da aula conforme enum Horario", example = "A")
  private Horario horario;

  @Schema(description = "Lista de recursos reservados para esta turma neste horário", example = "[]")
  private List<RecursoReservadoDTO> recursosReservados;
}

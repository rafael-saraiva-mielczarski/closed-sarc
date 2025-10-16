package com.closed_sarc.app_registration_api.application.dto;

import com.closed_sarc.app_registration_api.domain.entities.Horario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informações de uma aula específica")
public class AulaDTO {

  @Schema(description = "Nome da sala ou recurso onde a aula ocorre", example = "401")
  private String recurso;

  @Schema(description = "Nome do professor responsável pela disciplina", example = "Prof. Maria Silva")
  private String nomeProfessor;

  @Schema(description = "Nome da disciplina", example = "Programação Java")
  private String nomeDisciplina;

  @Schema(description = "Identificação da turma entre parênteses", example = "(33A)")
  private String turma;

  @Schema(description = "Horário da aula conforme enum Horario", example = "A")
  private Horario horario;
}

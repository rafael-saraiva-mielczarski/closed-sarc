package com.closed_sarc.app_registration_api.application.dto;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informações de um evento específico")
public class EventoDTO {

  @Schema(description = "Título do evento", example = "Reunião de planejamento")
  private String titulo;

  @Schema(description = "Descrição do evento", example = "Reunião para planejamento das próximas atividades")
  private String descricao;

  @Schema(description = "Data e hora de início do evento", example = "2024-10-16T08:00:00Z")
  private Instant dataInicio;

  @Schema(description = "Data e hora de fim do evento", example = "2024-10-16T09:30:00Z")
  private Instant dataFim;
}


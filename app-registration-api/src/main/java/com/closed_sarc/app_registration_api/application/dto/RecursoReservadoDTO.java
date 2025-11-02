package com.closed_sarc.app_registration_api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informações de um recurso reservado para uma turma")
public class RecursoReservadoDTO {

    @Schema(description = "ID do recurso", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID recursoId;

    @Schema(description = "Nome do recurso", example = "Caixa de Som")
    private String nomeRecurso;

    @Schema(description = "Tipo do recurso", example = "Áudio")
    private String tipoRecurso;

    @Schema(description = "Quantidade reservada", example = "2")
    private Integer quantidade;
}


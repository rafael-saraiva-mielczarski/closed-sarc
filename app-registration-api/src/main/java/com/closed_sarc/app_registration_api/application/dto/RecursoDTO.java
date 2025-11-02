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
@Schema(description = "DTO de resposta para recurso")
public class RecursoDTO {
    @Schema(description = "ID do recurso", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;
    
    @Schema(description = "Nome do recurso", example = "Sala 401")
    private String nome;
    
    @Schema(description = "Tipo do recurso", example = "Sala de Aula")
    private String tipo;
    
    @Schema(description = "Quantidade disponível do recurso", example = "1")
    private Integer quantidade;
    
    @Schema(description = "Capacidade do recurso (se aplicável)", example = "40")
    private Integer capacidade;
    
    @Schema(description = "Indica se o recurso está ativo", example = "true")
    private Boolean ativo;
}

package com.closed_sarc.app_registration_api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para requisição de criação de recurso")
public class RecursoRequestDTO {
    @Schema(description = "Nome do recurso", example = "Sala 401", required = true)
    private String nome;
    
    @Schema(description = "Tipo do recurso", example = "Sala de Aula")
    private String tipo;
    
    @Schema(description = "Quantidade disponível do recurso", example = "1")
    private Integer quantidade;
    
    @Schema(description = "Capacidade do recurso (se aplicável)", example = "40")
    private Integer capacidade;
}

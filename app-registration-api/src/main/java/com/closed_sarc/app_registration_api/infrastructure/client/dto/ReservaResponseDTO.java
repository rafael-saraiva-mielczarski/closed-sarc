package com.closed_sarc.app_registration_api.infrastructure.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de resposta de reserva")
public class ReservaResponseDTO {
    @Schema(description = "ID da reserva", example = "770e8400-e29b-41d4-a716-446655440002")
    private UUID id;
    
    @Schema(description = "ID do usuário que fez a reserva", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID usuarioId;
    
    @Schema(description = "ID da turma", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID turmaId;
    
    @Schema(description = "Recurso reservado")
    private RecursoDTO recurso;
    
    @Schema(description = "Data e hora em que a reserva foi criada", example = "2025-01-19T10:30:00Z")
    private Instant dataReserva;
    
    @Schema(description = "Data e hora de uso do recurso", example = "2025-01-20T08:00:00Z")
    private Instant dataUso;
    
    @Schema(description = "Quantidade reservada", example = "1")
    private Integer quantidade;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "DTO do recurso na reserva")
    public static class RecursoDTO {
        @Schema(description = "ID do recurso", example = "550e8400-e29b-41d4-a716-446655440000")
        private UUID id;
        
        @Schema(description = "Nome do recurso", example = "Sala 401")
        private String nome;
        
        @Schema(description = "Tipo do recurso", example = "Sala de Aula")
        private String tipo;
        
        @Schema(description = "Quantidade total do recurso", example = "1")
        private Integer quantidade;
        
        @Schema(description = "Capacidade do recurso", example = "40")
        private Integer capacidade;
        
        @Schema(description = "Indica se o recurso está ativo", example = "true")
        private Boolean ativo;
    }
}


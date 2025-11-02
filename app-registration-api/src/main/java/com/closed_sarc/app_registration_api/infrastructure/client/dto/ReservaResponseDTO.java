package com.closed_sarc.app_registration_api.infrastructure.client.dto;

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
public class ReservaResponseDTO {
    private UUID id;
    private UUID usuarioId;
    private UUID turmaId;
    private RecursoDTO recurso;
    private Instant dataReserva;
    private Instant dataUso;
    private Integer quantidade;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecursoDTO {
        private UUID id;
        private String nome;
        private String tipo;
        private Integer quantidade;
        private Integer capacidade;
        private Boolean ativo;
    }
}


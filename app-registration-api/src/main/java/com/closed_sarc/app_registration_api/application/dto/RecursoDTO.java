package com.closed_sarc.app_registration_api.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecursoDTO {
    private UUID id;
    private String nome;
    private String tipo;
    private Integer quantidade;
    private Integer capacidade;
    private Boolean ativo;
}

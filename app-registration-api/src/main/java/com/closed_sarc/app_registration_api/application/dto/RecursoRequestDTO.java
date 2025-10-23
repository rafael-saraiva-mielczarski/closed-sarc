package com.closed_sarc.app_registration_api.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecursoRequestDTO {
    private String nome;
    private String tipo;
    private Integer quantidade;
    private Integer capacidade;
}

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
@Schema(description = "Requisição para alteração de senha do usuário autenticado")
public class TrocarSenhaRequestDTO {

    @Schema(description = "Senha atual do usuário", example = "senhaAntiga123")
    private String senhaAtual;

    @Schema(description = "Nova senha que será definida para o usuário", example = "novaSenhaSegura456")
    private String novaSenha;
}

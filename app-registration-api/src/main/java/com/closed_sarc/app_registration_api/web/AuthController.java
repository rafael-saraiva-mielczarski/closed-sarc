package com.closed_sarc.app_registration_api.web;

import com.closed_sarc.app_registration_api.domain.entities.Usuario;
import com.closed_sarc.app_registration_api.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.closed_sarc.app_registration_api.application.dto.TrocarSenhaRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.senha()));

            return ResponseEntity.ok("Login realizado com sucesso: " + authentication.getName());
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }
    }

    @PutMapping("/trocar-senha")
    @Operation(summary = "Trocar senha",
            description = "Permite que o usuário autenticado altere sua senha atual.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "Senha alterada com sucesso"))),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    public ResponseEntity<?> changePassword(Authentication authentication,
                                            @RequestBody TrocarSenhaRequestDTO request) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();

            if (!passwordEncoder.matches(request.getSenhaAtual(), usuario.getSenha())) {
                return ResponseEntity.badRequest().body("Senha atual incorreta");
            }

            usuario.setSenha(passwordEncoder.encode(request.getNovaSenha()));
            usuarioService.salvar(usuario);

            return ResponseEntity.ok("Senha alterada com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao alterar senha");
        }
    }

    public record LoginRequest(String email, String senha) {
    }
}

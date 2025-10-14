package com.closed_sarc.app_registration_api.config;

import com.closed_sarc.app_registration_api.domain.entities.TipoUsuario;
import com.closed_sarc.app_registration_api.domain.entities.Usuario;
import com.closed_sarc.app_registration_api.domain.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        boolean adminExists = usuarioRepository.findAll().stream()
                .anyMatch(user -> user.getTipo() == TipoUsuario.ADMIN);

        if (!adminExists) {
            Usuario admin = Usuario.builder()
                    .nome("Administrador")
                    .email("admin@closed-sarc.com")
                    .senha(passwordEncoder.encode("admin123"))
                    .tipo(TipoUsuario.ADMIN)
                    .dataCriacao(Instant.now())
                    .build();

            usuarioRepository.save(admin);
        }
    }
}


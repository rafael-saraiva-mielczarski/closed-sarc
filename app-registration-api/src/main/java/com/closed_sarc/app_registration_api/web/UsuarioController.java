package com.closed_sarc.app_registration_api.web;

import com.closed_sarc.app_registration_api.domain.entities.Usuario;
import com.closed_sarc.app_registration_api.domain.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<Usuario> create(@RequestBody Usuario usuario) {
        usuario.setDataCriacao(Instant.now());
        Usuario saved = usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getById(@PathVariable UUID id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

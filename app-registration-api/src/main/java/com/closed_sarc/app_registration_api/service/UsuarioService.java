package com.closed_sarc.app_registration_api.service;

import com.closed_sarc.app_registration_api.domain.entities.Usuario;
import com.closed_sarc.app_registration_api.domain.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario create(Usuario usuario) {
        usuario.setDataCriacao(Instant.now());
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario findById(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public void deleteById(UUID id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        return usuarioRepository.existsById(id);
    }
}

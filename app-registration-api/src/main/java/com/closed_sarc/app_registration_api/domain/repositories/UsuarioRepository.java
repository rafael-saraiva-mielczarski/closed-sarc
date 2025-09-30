package com.closed_sarc.app_registration_api.domain.repositories;

import com.closed_sarc.app_registration_api.domain.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    boolean existsByEmail(String email);
}

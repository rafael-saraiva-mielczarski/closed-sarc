package com.closed_sarc.app_registration_api.domain.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.closed_sarc.app_registration_api.domain.entities.Evento;

public interface EventoRepository extends JpaRepository<Evento, UUID> {
    
    @Query("SELECT e FROM Evento e WHERE e.usuario.id = :usuarioId")
    List<Evento> findByUsuarioId(@Param("usuarioId") UUID usuarioId);
}
package com.closed_sarc.app_registration_api.domain.repositories;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.closed_sarc.app_registration_api.domain.entities.Evento;

public interface EventoRepository extends JpaRepository<Evento, UUID> {
    
    @Query("SELECT e FROM Evento e WHERE e.usuario.id = :usuarioId")
    List<Evento> findByUsuarioId(@Param("usuarioId") UUID usuarioId);

    @Query(value = "SELECT * FROM evento e WHERE " +
           "(DATE(e.data_inicio) = DATE(:data) OR " +
           "(DATE(e.data_inicio) <= DATE(:data) AND " +
           "(e.data_fim IS NULL OR DATE(e.data_fim) >= DATE(:data))))",
           nativeQuery = true)
    List<Evento> findByData(@Param("data") Instant data);
}
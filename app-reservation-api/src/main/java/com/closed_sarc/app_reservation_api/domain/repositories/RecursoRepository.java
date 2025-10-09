package com.closed_sarc.app_reservation_api.domain.repositories;

import com.closed_sarc.app_reservation_api.domain.entities.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RecursoRepository extends JpaRepository<Recurso, UUID> {
}

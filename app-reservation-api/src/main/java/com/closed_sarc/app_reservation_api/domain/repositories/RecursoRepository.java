package com.closed_sarc.reservation.repository;

import com.closed_sarc.reservation.domain.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RecursoRepository extends JpaRepository<Recurso, UUID> {
}

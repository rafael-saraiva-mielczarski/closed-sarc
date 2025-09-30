package com.closed_sarc.reservation.repository;

import com.closed_sarc.reservation.domain.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ReservaRepository extends JpaRepository<Reserva, UUID> {
}

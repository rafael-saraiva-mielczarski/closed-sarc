package com.closed_sarc.app_reservation_api.domain.repositories;

import com.closed_sarc.app_reservation_api.domain.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ReservaRepository extends JpaRepository<Reserva, UUID> {
}

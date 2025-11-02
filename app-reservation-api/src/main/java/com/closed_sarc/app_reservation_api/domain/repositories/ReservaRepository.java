package com.closed_sarc.app_reservation_api.domain.repositories;

import com.closed_sarc.app_reservation_api.domain.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ReservaRepository extends JpaRepository<Reserva, UUID> {
    List<Reserva> findByTurmaId(UUID turmaId);
    
    List<Reserva> findByTurmaIdAndDataUso(UUID turmaId, Instant dataUso);
    
    @Query("SELECT r FROM Reserva r WHERE r.recurso.id = :recursoId AND r.dataUso = :dataUso")
    List<Reserva> findByRecursoIdAndDataUso(@Param("recursoId") UUID recursoId, @Param("dataUso") Instant dataUso);
}

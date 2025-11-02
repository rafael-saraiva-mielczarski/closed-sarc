package com.closed_sarc.app_reservation_api.domain.entities;

import lombok.*;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reserva")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID usuarioId;
    private UUID turmaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recurso_id")
    private Recurso recurso;

    @Column(name = "data_reserva")
    private Instant dataReserva;

    @Column(name = "data_uso")
    private Instant dataUso;

    @Column(nullable = false)
    private Integer quantidade;
}

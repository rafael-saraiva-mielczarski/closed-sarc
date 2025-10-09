package com.closed_sarc.app_reservation_api.domain.entities;

import lombok.*;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "evento")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evento {
    @Id
    @GeneratedValue
    private UUID id;

    private String titulo;

    @Column(columnDefinition = "text")
    private String descricao;

    @Column(name="data_inicio")
    private Instant dataInicio;

    @Column(name="data_fim")
    private Instant dataFim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;
}

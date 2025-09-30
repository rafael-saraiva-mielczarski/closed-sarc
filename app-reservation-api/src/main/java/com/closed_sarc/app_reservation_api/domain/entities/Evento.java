package com.closed_sarc.reservation.domain;

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

    private UUID reservaId;
}

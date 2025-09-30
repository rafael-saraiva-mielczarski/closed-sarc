package com.closed_sarc.app_registration_api.domain.entities;

import lombok.*;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cronograma")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cronograma {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID turmaId;

    @Column(name = "data_hora")
    private Instant dataHora;

    @Column(length = 100)
    private String sala;

    @Column(length = 255)
    private String materia;
}

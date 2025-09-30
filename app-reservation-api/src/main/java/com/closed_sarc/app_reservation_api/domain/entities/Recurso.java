package com.closed_sarc.reservation.domain;

import lombok.*;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "recurso")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recurso {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String nome;

    private String tipo;

    private Integer quantidade;

    private Integer capacidade;
}

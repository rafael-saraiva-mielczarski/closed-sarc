package com.closed_sarc.app_registration_api.domain.entities;

import lombok.*;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String senha; // hash!

    @Column(nullable = false)
    private String tipo;

    @Column(name = "data_criacao", nullable = false)
    private Instant dataCriacao;
}

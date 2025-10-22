package com.closed_sarc.app_reservation_api.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "recurso")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Recurso {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(length = 100)
    private String tipo;

    private Integer quantidade;

    private Integer capacidade;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;
}

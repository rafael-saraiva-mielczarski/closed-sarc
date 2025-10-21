package com.closed_sarc.app_registration_api.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "disciplina")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Disciplina {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(columnDefinition = "text")
    private String descricao;

    private Integer cargaHoraria;
}

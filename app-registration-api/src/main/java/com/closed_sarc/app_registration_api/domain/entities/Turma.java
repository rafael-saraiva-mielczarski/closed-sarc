package com.closed_sarc.app_registration_api.domain.entities;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "turma")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Turma {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Semestre semestre;

    @Column(nullable = false)
    private Integer ano;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 5)
    private Horario horario;

    @ElementCollection
    @CollectionTable(name = "turma_dias_aula", joinColumns = @JoinColumn(name = "turma_id"))
    @Column(name = "dia_semana", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<DiaSemana> diasAula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    private Usuario professor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;
}

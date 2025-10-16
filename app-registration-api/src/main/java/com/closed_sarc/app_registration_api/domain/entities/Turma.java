package com.closed_sarc.app_registration_api.domain.entities;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false, length = 100)
    private String sala;

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

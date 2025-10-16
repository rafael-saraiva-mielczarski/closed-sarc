package com.closed_sarc.app_registration_api.domain.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.closed_sarc.app_registration_api.domain.entities.DiaSemana;
import com.closed_sarc.app_registration_api.domain.entities.Turma;

public interface TurmaRepository extends JpaRepository<Turma, UUID> {

  @Query("SELECT t FROM Turma t " +
      "JOIN FETCH t.professor " +
      "JOIN FETCH t.disciplina " +
      "WHERE :diaSemana MEMBER OF t.diasAula")
  List<Turma> findByDiasAulaContaining(@Param("diaSemana") DiaSemana diaSemana);
}

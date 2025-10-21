package com.closed_sarc.app_registration_api.domain.repositories;

import com.closed_sarc.app_registration_api.domain.entities.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DisciplinaRepository extends JpaRepository<Disciplina, UUID> {
    Optional<Disciplina> findByNomeIgnoreCase(String nome);
    List<Disciplina> findByNomeContainingIgnoreCase(String nome);
}

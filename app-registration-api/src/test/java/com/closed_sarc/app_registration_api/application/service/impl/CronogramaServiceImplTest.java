package com.closed_sarc.app_registration_api.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.closed_sarc.app_registration_api.application.dto.AulaDTO;
import com.closed_sarc.app_registration_api.application.dto.CronogramaDTO;
import com.closed_sarc.app_registration_api.domain.entities.DiaSemana;
import com.closed_sarc.app_registration_api.domain.entities.Disciplina;
import com.closed_sarc.app_registration_api.domain.entities.Horario;
import com.closed_sarc.app_registration_api.domain.entities.Turma;
import com.closed_sarc.app_registration_api.domain.entities.Usuario;
import com.closed_sarc.app_registration_api.domain.repositories.TurmaRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CronogramaServiceImpl - Testes Unitários")
class CronogramaServiceImplTest {

  @Mock
  private TurmaRepository turmaRepository;

  @InjectMocks
  private CronogramaServiceImpl cronogramaService;

  private Usuario professor;
  private Disciplina disciplina;
  private Turma turma;

  @BeforeEach
  void setUp() {
    // Criar objetos de teste
    professor = Usuario.builder()
        .nome("Prof. Maria Silva")
        .build();

    disciplina = Disciplina.builder()
        .nome("Programação Java")
        .build();

    turma = Turma.builder()
        .nome("33A")
        .horario(Horario.A)
        .professor(professor)
        .disciplina(disciplina)
        .build();
  }

  @Test
  @DisplayName("Deve retornar cronograma com aulas quando existem turmas")
  void deveRetornarCronogramaComAulasQuandoExistemTurmas() {
    // Given
    List<Turma> turmasEsperadas = Arrays.asList(turma);

    when(turmaRepository.findByDiasAulaContaining(any(DiaSemana.class)))
        .thenReturn(turmasEsperadas);

    // When
    CronogramaDTO resultado = cronogramaService.consultarCronograma();

    // Then
    assertNotNull(resultado);
    assertNotNull(resultado.getAulasDeHoje());
    assertEquals(1, resultado.getAulasDeHoje().size());

    AulaDTO aula = resultado.getAulasDeHoje().get(0);
    assertEquals("Prof. Maria Silva", aula.getNomeProfessor());
    assertEquals("Programação Java", aula.getNomeDisciplina());
    assertEquals("(33A)", aula.getTurma());
    assertEquals(Horario.A, aula.getHorario());

    verify(turmaRepository).findByDiasAulaContaining(any(DiaSemana.class));
  }

  @Test
  @DisplayName("Deve retornar cronograma vazio quando não existem turmas")
  void deveRetornarCronogramaVazioQuandoNaoExistemTurmas() {
    // Given
    when(turmaRepository.findByDiasAulaContaining(any(DiaSemana.class)))
        .thenReturn(Collections.emptyList());

    // When
    CronogramaDTO resultado = cronogramaService.consultarCronograma();

    // Then
    assertNotNull(resultado);
    assertNotNull(resultado.getAulasDeHoje());
    assertTrue(resultado.getAulasDeHoje().isEmpty());

    verify(turmaRepository).findByDiasAulaContaining(any(DiaSemana.class));
  }

  @Test
  @DisplayName("Deve retornar múltiplas aulas quando existem várias turmas")
  void deveRetornarMultiplasAulasQuandoExistemVariasTurmas() {
    // Given
    Usuario professor2 = Usuario.builder()
        .nome("Prof. Ana Costa")
        .build();

    Disciplina disciplina2 = Disciplina.builder()
        .nome("Desenvolvimento Web")
        .build();

    Turma turma2 = Turma.builder()
        .nome("33C")
        .horario(Horario.E)
        .professor(professor2)
        .disciplina(disciplina2)
        .build();

    List<Turma> turmasEsperadas = Arrays.asList(turma, turma2);

    when(turmaRepository.findByDiasAulaContaining(any(DiaSemana.class)))
        .thenReturn(turmasEsperadas);

    // When
    CronogramaDTO resultado = cronogramaService.consultarCronograma();

    // Then
    assertNotNull(resultado);
    assertEquals(2, resultado.getAulasDeHoje().size());

    // Verificar primeira aula
    AulaDTO aula1 = resultado.getAulasDeHoje().get(0);
    assertEquals("Prof. Maria Silva", aula1.getNomeProfessor());
    assertEquals("Programação Java", aula1.getNomeDisciplina());

    // Verificar segunda aula
    AulaDTO aula2 = resultado.getAulasDeHoje().get(1);
    assertEquals("Prof. Ana Costa", aula2.getNomeProfessor());
    assertEquals("Desenvolvimento Web", aula2.getNomeDisciplina());

    verify(turmaRepository).findByDiasAulaContaining(any(DiaSemana.class));
  }
}

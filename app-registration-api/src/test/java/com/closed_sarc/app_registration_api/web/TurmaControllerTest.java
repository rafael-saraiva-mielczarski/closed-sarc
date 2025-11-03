package com.closed_sarc.app_registration_api.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.closed_sarc.app_registration_api.config.TestSecurityConfig;
import com.closed_sarc.app_registration_api.domain.entities.DiaSemana;
import com.closed_sarc.app_registration_api.domain.entities.Horario;
import com.closed_sarc.app_registration_api.domain.entities.Semestre;
import com.closed_sarc.app_registration_api.domain.entities.Turma;
import com.closed_sarc.app_registration_api.service.TurmaService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TurmaController.class)
@TestPropertySource("classpath:application-test.properties")
@Import(TestSecurityConfig.class)
@DisplayName("TurmaController - Testes de Integração")
class TurmaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TurmaService turmaService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("POST /api/turmas - Deve retornar 201 e criar turma quando dados válidos")
  void deveRetornar201EcriarTurmaQuandoDadosValidos() throws Exception {
    // Given
    UUID professorId = UUID.randomUUID();
    UUID disciplinaId = UUID.randomUUID();

    TurmaController.TurmaRequest request = new TurmaController.TurmaRequest(
        "33A",
        Semestre.PRIMEIRO,
        2025,
        Horario.A,
        Arrays.asList(DiaSemana.SEGUNDA, DiaSemana.QUARTA),
        professorId,
        disciplinaId);

    Turma turmaCriada = Turma.builder()
        .id(UUID.randomUUID())
        .nome("33A")
        .semestre(Semestre.PRIMEIRO)
        .ano(2025)
        .horario(Horario.A)
        .diasAula(Arrays.asList(DiaSemana.SEGUNDA, DiaSemana.QUARTA))
        .build();

    when(turmaService.create(org.mockito.ArgumentMatchers.any(Turma.class),
        org.mockito.ArgumentMatchers.eq(professorId),
        org.mockito.ArgumentMatchers.eq(disciplinaId)))
        .thenReturn(turmaCriada);

    // When & Then
    mockMvc.perform(post("/api/turmas")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.nome").value("33A"))
        .andExpect(jsonPath("$.semestre").value("PRIMEIRO"))
        .andExpect(jsonPath("$.ano").value(2025))
        .andExpect(jsonPath("$.horario").value("A"));
  }

  @Test
  @DisplayName("POST /api/turmas - Deve retornar 400 quando dados inválidos")
  void deveRetornar400QuandoDadosInvalidos() throws Exception {
    // Given
    UUID professorId = UUID.randomUUID();
    UUID disciplinaId = UUID.randomUUID();

    TurmaController.TurmaRequest request = new TurmaController.TurmaRequest(
        "33A",
        Semestre.PRIMEIRO,
        2025,
        Horario.A,
        Arrays.asList(DiaSemana.SEGUNDA, DiaSemana.QUARTA),
        professorId,
        disciplinaId);

    when(turmaService.create(org.mockito.ArgumentMatchers.any(Turma.class),
        org.mockito.ArgumentMatchers.eq(professorId),
        org.mockito.ArgumentMatchers.eq(disciplinaId)))
        .thenThrow(new IllegalArgumentException("Professor não encontrado"));

    // When & Then
    mockMvc.perform(post("/api/turmas")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("Professor não encontrado"));
  }

  @Test
  @DisplayName("GET /api/turmas/{id} - Deve retornar 200 e turma quando ID válido")
  void deveRetornar200ETurmaQuandoIdValido() throws Exception {
    // Given
    UUID turmaId = UUID.randomUUID();
    Turma turma = Turma.builder()
        .id(turmaId)
        .nome("33A")
        .semestre(Semestre.PRIMEIRO)
        .ano(2025)
        .horario(Horario.A)
        .diasAula(Arrays.asList(DiaSemana.SEGUNDA, DiaSemana.QUARTA))
        .build();

    when(turmaService.findById(turmaId)).thenReturn(turma);

    // When & Then
    mockMvc.perform(get("/api/turmas/" + turmaId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(turmaId.toString()))
        .andExpect(jsonPath("$.nome").value("33A"))
        .andExpect(jsonPath("$.semestre").value("PRIMEIRO"))
        .andExpect(jsonPath("$.ano").value(2025))
        .andExpect(jsonPath("$.horario").value("A"));
  }

  @Test
  @DisplayName("GET /api/turmas/{id} - Deve retornar 404 quando turma não encontrada")
  void deveRetornar404QuandoTurmaNaoEncontrada() throws Exception {
    // Given
    UUID turmaId = UUID.randomUUID();
    when(turmaService.findById(turmaId))
        .thenThrow(new IllegalArgumentException("Turma não encontrada"));

    // When & Then
    mockMvc.perform(get("/api/turmas/" + turmaId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET /api/turmas - Deve retornar 200 e lista de turmas")
  void deveRetornar200EListaTurmas() throws Exception {
    // Given
    Turma turma1 = Turma.builder()
        .id(UUID.randomUUID())
        .nome("33A")
        .semestre(Semestre.PRIMEIRO)
        .ano(2025)
        .horario(Horario.A)
        .diasAula(Arrays.asList(DiaSemana.SEGUNDA, DiaSemana.QUARTA))
        .build();

    Turma turma2 = Turma.builder()
        .id(UUID.randomUUID())
        .nome("33B")
        .semestre(Semestre.PRIMEIRO)
        .ano(2025)
        .horario(Horario.B)
        .diasAula(Arrays.asList(DiaSemana.TERCA, DiaSemana.QUINTA))
        .build();

    List<Turma> turmas = Arrays.asList(turma1, turma2);
    when(turmaService.findAll()).thenReturn(turmas);

    // When & Then
    mockMvc.perform(get("/api/turmas")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].nome").value("33A"))
        .andExpect(jsonPath("$[1].nome").value("33B"));
  }

  @Test
  @DisplayName("GET /api/turmas - Deve retornar 200 e lista vazia quando não há turmas")
  void deveRetornar200EListaVaziaQuandoNaoHaTurmas() throws Exception {
    // Given
    when(turmaService.findAll()).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(get("/api/turmas")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @DisplayName("GET /api/turmas/professor/{professorId} - Deve retornar 200 e turmas do professor")
  void deveRetornar200ETurmasDoProf() throws Exception {
    // Given
    UUID professorId = UUID.randomUUID();
    Turma turma = Turma.builder()
        .id(UUID.randomUUID())
        .nome("33A")
        .semestre(Semestre.PRIMEIRO)
        .ano(2025)
        .horario(Horario.A)
        .diasAula(Arrays.asList(DiaSemana.SEGUNDA, DiaSemana.QUARTA))
        .build();

    List<Turma> turmas = Arrays.asList(turma);
    when(turmaService.findByProfessorId(professorId)).thenReturn(turmas);

    // When & Then
    mockMvc.perform(get("/api/turmas/professor/" + professorId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].nome").value("33A"));
  }

  @Test
  @DisplayName("GET /api/turmas/disciplina/{disciplinaId} - Deve retornar 200 e turmas da disciplina")
  void deveRetornar200ETurmasDaDisciplina() throws Exception {
    // Given
    UUID disciplinaId = UUID.randomUUID();
    Turma turma = Turma.builder()
        .id(UUID.randomUUID())
        .nome("33A")
        .semestre(Semestre.PRIMEIRO)
        .ano(2025)
        .horario(Horario.A)
        .diasAula(Arrays.asList(DiaSemana.SEGUNDA, DiaSemana.QUARTA))
        .build();

    List<Turma> turmas = Arrays.asList(turma);
    when(turmaService.findByDisciplinaId(disciplinaId)).thenReturn(turmas);

    // When & Then
    mockMvc.perform(get("/api/turmas/disciplina/" + disciplinaId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].nome").value("33A"));
  }
}

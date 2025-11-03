package com.closed_sarc.app_registration_api.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.closed_sarc.app_registration_api.application.service.ReservaService;
import com.closed_sarc.app_registration_api.domain.entities.Horario;
import com.closed_sarc.app_registration_api.infrastructure.client.dto.ReservaResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWebMvc
@TestPropertySource("classpath:application-test.properties")
@DisplayName("ReservaController - Testes de Integração")
class ReservaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ReservaService reservaService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("POST /api/reservas - Deve retornar 201 e criar reserva quando dados válidos")
  void deveRetornar201EcriarReservaQuandoDadosValidos() throws Exception {
    // Given
    UUID turmaId = UUID.randomUUID();
    UUID recursoId = UUID.randomUUID();

    ReservaController.ReservaRequest request = new ReservaController.ReservaRequest(
        turmaId,
        recursoId,
        1,
        LocalDate.of(2025, 1, 20),
        Horario.A);

    ReservaResponseDTO.RecursoDTO recursoDTO = ReservaResponseDTO.RecursoDTO.builder()
        .id(recursoId)
        .nome("Sala 401")
        .tipo("Sala de Aula")
        .quantidade(1)
        .capacidade(40)
        .ativo(true)
        .build();

    ReservaResponseDTO reservaCriada = ReservaResponseDTO.builder()
        .id(UUID.randomUUID())
        .usuarioId(UUID.randomUUID())
        .turmaId(turmaId)
        .recurso(recursoDTO)
        .dataReserva(Instant.now())
        .dataUso(Instant.parse("2025-01-20T08:00:00Z"))
        .quantidade(1)
        .build();

    when(reservaService.reservarRecursoParaTurma(turmaId, recursoId, 1,
        LocalDate.of(2025, 1, 20), Horario.A))
        .thenReturn(reservaCriada);

    // When & Then
    mockMvc.perform(post("/api/reservas")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.turmaId").value(turmaId.toString()))
        .andExpect(jsonPath("$.quantidade").value(1))
        .andExpect(jsonPath("$.recurso.nome").value("Sala 401"));
  }

  @Test
  @DisplayName("POST /api/reservas - Deve retornar 400 quando turma não tem aula na data/horário")
  void deveRetornar400QuandoTurmaNaoTemAulaNaDataHorario() throws Exception {
    // Given
    UUID turmaId = UUID.randomUUID();
    UUID recursoId = UUID.randomUUID();

    ReservaController.ReservaRequest request = new ReservaController.ReservaRequest(
        turmaId,
        recursoId,
        1,
        LocalDate.of(2025, 1, 21),
        Horario.A);

    when(reservaService.reservarRecursoParaTurma(turmaId, recursoId, 1,
        LocalDate.of(2025, 1, 21), Horario.A))
        .thenThrow(new IllegalArgumentException("A data 2025-01-21 não é uma data de aula válida para a turma"));

    // When & Then
    mockMvc.perform(post("/api/reservas")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("A data 2025-01-21 não é uma data de aula válida para a turma"));
  }

  @Test
  @DisplayName("GET /api/reservas/turma/{turmaId} - Deve retornar 200 e reservas da turma")
  void deveRetornar200EReservasDaTurma() throws Exception {
    // Given
    UUID turmaId = UUID.randomUUID();
    UUID recursoId = UUID.randomUUID();

    ReservaResponseDTO.RecursoDTO recursoDTO = ReservaResponseDTO.RecursoDTO.builder()
        .id(recursoId)
        .nome("Sala 401")
        .tipo("Sala de Aula")
        .quantidade(1)
        .capacidade(40)
        .ativo(true)
        .build();

    ReservaResponseDTO reserva = ReservaResponseDTO.builder()
        .id(UUID.randomUUID())
        .usuarioId(UUID.randomUUID())
        .turmaId(turmaId)
        .recurso(recursoDTO)
        .dataReserva(Instant.now())
        .dataUso(Instant.parse("2025-01-20T08:00:00Z"))
        .quantidade(1)
        .build();

    List<ReservaResponseDTO> reservas = Arrays.asList(reserva);
    when(reservaService.buscarReservasPorTurma(turmaId)).thenReturn(reservas);

    // When & Then
    mockMvc.perform(get("/api/reservas/turma/" + turmaId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].turmaId").value(turmaId.toString()))
        .andExpect(jsonPath("$[0].recurso.nome").value("Sala 401"));
  }

  @Test
  @DisplayName("GET /api/reservas/turma/{turmaId} - Deve retornar 200 e lista vazia quando não há reservas")
  void deveRetornar200EListaVaziaQuandoNaoHaReservas() throws Exception {
    // Given
    UUID turmaId = UUID.randomUUID();
    when(reservaService.buscarReservasPorTurma(turmaId)).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(get("/api/reservas/turma/" + turmaId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @DisplayName("GET /api/reservas/turma/{turmaId}/data - Deve retornar 200 e reservas por data e horário")
  void deveRetornar200EReservasPorDataEHorario() throws Exception {
    // Given
    UUID turmaId = UUID.randomUUID();
    UUID recursoId = UUID.randomUUID();
    LocalDate data = LocalDate.of(2025, 1, 20);
    Horario horario = Horario.A;

    ReservaResponseDTO.RecursoDTO recursoDTO = ReservaResponseDTO.RecursoDTO.builder()
        .id(recursoId)
        .nome("Sala 401")
        .tipo("Sala de Aula")
        .quantidade(1)
        .capacidade(40)
        .ativo(true)
        .build();

    ReservaResponseDTO reserva = ReservaResponseDTO.builder()
        .id(UUID.randomUUID())
        .usuarioId(UUID.randomUUID())
        .turmaId(turmaId)
        .recurso(recursoDTO)
        .dataReserva(Instant.now())
        .dataUso(Instant.parse("2025-01-20T08:00:00Z"))
        .quantidade(1)
        .build();

    List<ReservaResponseDTO> reservas = Arrays.asList(reserva);
    when(reservaService.buscarReservasPorTurmaEData(turmaId, data, horario)).thenReturn(reservas);

    // When & Then
    mockMvc.perform(get("/api/reservas/turma/" + turmaId + "/data")
        .param("data", "2025-01-20")
        .param("horario", "A")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].turmaId").value(turmaId.toString()))
        .andExpect(jsonPath("$[0].recurso.nome").value("Sala 401"));
  }
}

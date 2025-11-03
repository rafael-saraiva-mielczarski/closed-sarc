package com.closed_sarc.app_reservation_api.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
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

import com.closed_sarc.app_reservation_api.config.TestSecurityConfig;
import com.closed_sarc.app_reservation_api.domain.entities.Recurso;
import com.closed_sarc.app_reservation_api.domain.entities.Reserva;
import com.closed_sarc.app_reservation_api.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ReservationController.class)
@TestPropertySource("classpath:application-test.properties")
@Import(TestSecurityConfig.class)
@DisplayName("ReservationController - Testes de Integração")
class ReservationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ReservationService reservationService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("POST /api/reservas - Deve retornar 201 e criar reserva quando dados válidos")
  void deveRetornar201EcriarReservaQuandoDadosValidos() throws Exception {
    // Given
    UUID turmaId = UUID.randomUUID();
    UUID recursoId = UUID.randomUUID();
    Instant dataUso = Instant.parse("2025-01-20T08:00:00Z");

    ReservationController.ReservationRequest request = new ReservationController.ReservationRequest(
        turmaId,
        recursoId,
        1,
        dataUso);

    Recurso recurso = Recurso.builder()
        .id(recursoId)
        .nome("Sala 401")
        .tipo("Sala de Aula")
        .quantidade(1)
        .capacidade(40)
        .ativo(true)
        .build();

    Reserva reservaCriada = Reserva.builder()
        .id(UUID.randomUUID())
        .usuarioId(UUID.randomUUID())
        .turmaId(turmaId)
        .recurso(recurso)
        .dataReserva(Instant.now())
        .dataUso(dataUso)
        .quantidade(1)
        .build();

    when(reservationService.createReservation(turmaId, recursoId, 1, dataUso))
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
  @DisplayName("POST /api/reservas - Deve retornar 400 quando recurso não disponível")
  void deveRetornar400QuandoRecursoNaoDisponivel() throws Exception {
    // Given
    UUID turmaId = UUID.randomUUID();
    UUID recursoId = UUID.randomUUID();
    Instant dataUso = Instant.parse("2025-01-20T08:00:00Z");

    ReservationController.ReservationRequest request = new ReservationController.ReservationRequest(
        turmaId,
        recursoId,
        1,
        dataUso);

    when(reservationService.createReservation(turmaId, recursoId, 1, dataUso))
        .thenThrow(new IllegalArgumentException("Recurso não disponível"));

    // When & Then
    mockMvc.perform(post("/api/reservas")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("Recurso não disponível"));
  }

  @Test
  @DisplayName("GET /api/reservas/turma/{turmaId} - Deve retornar 200 e reservas da turma")
  void deveRetornar200EReservasDaTurma() throws Exception {
    // Given
    UUID turmaId = UUID.randomUUID();
    UUID recursoId = UUID.randomUUID();

    Recurso recurso = Recurso.builder()
        .id(recursoId)
        .nome("Sala 401")
        .tipo("Sala de Aula")
        .quantidade(1)
        .capacidade(40)
        .ativo(true)
        .build();

    Reserva reserva = Reserva.builder()
        .id(UUID.randomUUID())
        .usuarioId(UUID.randomUUID())
        .turmaId(turmaId)
        .recurso(recurso)
        .dataReserva(Instant.now())
        .dataUso(Instant.parse("2025-01-20T08:00:00Z"))
        .quantidade(1)
        .build();

    List<Reserva> reservas = Arrays.asList(reserva);
    when(reservationService.findReservationsByTurmaId(turmaId)).thenReturn(reservas);

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
    when(reservationService.findReservationsByTurmaId(turmaId)).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(get("/api/reservas/turma/" + turmaId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @DisplayName("GET /api/reservas/turma/{turmaId}/data - Deve retornar 200 e reservas por data")
  void deveRetornar200EReservasPorData() throws Exception {
    // Given
    UUID turmaId = UUID.randomUUID();
    UUID recursoId = UUID.randomUUID();
    Instant dataUso = Instant.parse("2025-01-20T08:00:00Z");

    Recurso recurso = Recurso.builder()
        .id(recursoId)
        .nome("Sala 401")
        .tipo("Sala de Aula")
        .quantidade(1)
        .capacidade(40)
        .ativo(true)
        .build();

    Reserva reserva = Reserva.builder()
        .id(UUID.randomUUID())
        .usuarioId(UUID.randomUUID())
        .turmaId(turmaId)
        .recurso(recurso)
        .dataReserva(Instant.now())
        .dataUso(dataUso)
        .quantidade(1)
        .build();

    List<Reserva> reservas = Arrays.asList(reserva);
    when(reservationService.findReservationsByTurmaAndDataUso(turmaId, dataUso)).thenReturn(reservas);

    // When & Then
    mockMvc.perform(get("/api/reservas/turma/" + turmaId + "/data")
        .param("dataUso", "2025-01-20T08:00:00Z")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].turmaId").value(turmaId.toString()))
        .andExpect(jsonPath("$[0].recurso.nome").value("Sala 401"));
  }
}

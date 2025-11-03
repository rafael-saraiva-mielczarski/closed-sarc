package com.closed_sarc.app_registration_api.web;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

import com.closed_sarc.app_registration_api.config.TestSecurityConfig;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.closed_sarc.app_registration_api.domain.entities.Evento;
import com.closed_sarc.app_registration_api.service.EventoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(EventoController.class)
@TestPropertySource("classpath:application-test.properties")
@Import(TestSecurityConfig.class)
@DisplayName("EventoController - Testes de Integração")
class EventoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private EventoService eventoService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("POST /api/eventos - Deve retornar 201 e criar evento quando dados válidos")
  void deveRetornar201EcriarEventoQuandoDadosValidos() throws Exception {
    // Given
    Evento eventoRequest = Evento.builder()
        .titulo("Reunião de planejamento")
        .descricao("Reunião para planejamento das próximas atividades")
        .dataInicio(Instant.parse("2024-10-16T08:00:00Z"))
        .dataFim(Instant.parse("2024-10-16T09:30:00Z"))
        .build();

    Evento eventoCriado = Evento.builder()
        .id(UUID.randomUUID())
        .titulo("Reunião de planejamento")
        .descricao("Reunião para planejamento das próximas atividades")
        .dataInicio(Instant.parse("2024-10-16T08:00:00Z"))
        .dataFim(Instant.parse("2024-10-16T09:30:00Z"))
        .build();

    when(eventoService.create(org.mockito.ArgumentMatchers.any(Evento.class)))
        .thenReturn(eventoCriado);

    // When & Then
    mockMvc.perform(post("/api/eventos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(eventoRequest)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.titulo").value("Reunião de planejamento"))
        .andExpect(jsonPath("$.descricao").value("Reunião para planejamento das próximas atividades"));
  }

  @Test
  @DisplayName("POST /api/eventos - Deve retornar 400 quando dados inválidos")
  void deveRetornar400QuandoDadosInvalidos() throws Exception {
    // Given
    Evento eventoRequest = Evento.builder()
        .titulo("Reunião de planejamento")
        .descricao("Reunião para planejamento das próximas atividades")
        .dataInicio(Instant.parse("2024-10-16T08:00:00Z"))
        .dataFim(Instant.parse("2024-10-16T09:30:00Z"))
        .build();

    when(eventoService.create(org.mockito.ArgumentMatchers.any(Evento.class)))
        .thenThrow(new RuntimeException("Título é obrigatório"));

    // When & Then
    mockMvc.perform(post("/api/eventos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(eventoRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("Título é obrigatório"));
  }

  @Test
  @DisplayName("GET /api/eventos - Deve retornar 200 e lista de eventos")
  void deveRetornar200EListaEventos() throws Exception {
    // Given
    Evento evento1 = Evento.builder()
        .id(UUID.randomUUID())
        .titulo("Reunião de planejamento")
        .descricao("Reunião para planejamento das próximas atividades")
        .dataInicio(Instant.parse("2024-10-16T08:00:00Z"))
        .dataFim(Instant.parse("2024-10-16T09:30:00Z"))
        .build();

    Evento evento2 = Evento.builder()
        .id(UUID.randomUUID())
        .titulo("Workshop de programação")
        .descricao("Workshop prático de programação")
        .dataInicio(Instant.parse("2024-10-16T14:00:00Z"))
        .dataFim(Instant.parse("2024-10-16T16:00:00Z"))
        .build();

    List<Evento> eventos = Arrays.asList(evento1, evento2);
    when(eventoService.findAll()).thenReturn(eventos);

    // When & Then
    mockMvc.perform(get("/api/eventos")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].titulo").value("Reunião de planejamento"))
        .andExpect(jsonPath("$[1].titulo").value("Workshop de programação"));
  }

  @Test
  @DisplayName("GET /api/eventos - Deve retornar 200 e lista vazia quando não há eventos")
  void deveRetornar200EListaVaziaQuandoNaoHaEventos() throws Exception {
    // Given
    when(eventoService.findAll()).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(get("/api/eventos")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @DisplayName("GET /api/eventos/{id} - Deve retornar 200 e evento quando ID válido")
  void deveRetornar200EEventoQuandoIdValido() throws Exception {
    // Given
    UUID eventoId = UUID.randomUUID();
    Evento evento = Evento.builder()
        .id(eventoId)
        .titulo("Reunião de planejamento")
        .descricao("Reunião para planejamento das próximas atividades")
        .dataInicio(Instant.parse("2024-10-16T08:00:00Z"))
        .dataFim(Instant.parse("2024-10-16T09:30:00Z"))
        .build();

    when(eventoService.findById(eventoId)).thenReturn(evento);

    // When & Then
    mockMvc.perform(get("/api/eventos/" + eventoId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(eventoId.toString()))
        .andExpect(jsonPath("$.titulo").value("Reunião de planejamento"))
        .andExpect(jsonPath("$.descricao").value("Reunião para planejamento das próximas atividades"));
  }

  @Test
  @DisplayName("GET /api/eventos/{id} - Deve retornar 400 quando evento não encontrado")
  void deveRetornar400QuandoEventoNaoEncontrado() throws Exception {
    // Given
    UUID eventoId = UUID.randomUUID();
    when(eventoService.findById(eventoId))
        .thenThrow(new RuntimeException("Evento não encontrado"));

    // When & Then
    mockMvc.perform(get("/api/eventos/" + eventoId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("Evento não encontrado"));
  }

  @Test
  @DisplayName("GET /api/eventos/usuario/{usuarioId} - Deve retornar 200 e eventos do usuário")
  void deveRetornar200EEventosDoUsuario() throws Exception {
    // Given
    UUID usuarioId = UUID.randomUUID();
    Evento evento = Evento.builder()
        .id(UUID.randomUUID())
        .titulo("Reunião de planejamento")
        .descricao("Reunião para planejamento das próximas atividades")
        .dataInicio(Instant.parse("2024-10-16T08:00:00Z"))
        .dataFim(Instant.parse("2024-10-16T09:30:00Z"))
        .build();

    List<Evento> eventos = Arrays.asList(evento);
    when(eventoService.findByUsuarioId(usuarioId)).thenReturn(eventos);

    // When & Then
    mockMvc.perform(get("/api/eventos/usuario/" + usuarioId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].titulo").value("Reunião de planejamento"));
  }

  @Test
  @DisplayName("DELETE /api/eventos/{id} - Deve retornar 204 quando evento excluído com sucesso")
  void deveRetornar204QuandoEventoExcluidoComSucesso() throws Exception {
    // Given
    UUID eventoId = UUID.randomUUID();
    doNothing().when(eventoService).delete(eventoId);

    // When & Then
    mockMvc.perform(delete("/api/eventos/" + eventoId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/eventos/{id} - Deve retornar 400 quando evento não encontrado para exclusão")
  void deveRetornar400QuandoEventoNaoEncontradoParaExclusao() throws Exception {
    // Given
    UUID eventoId = UUID.randomUUID();
    doThrow(new RuntimeException("Evento não encontrado"))
        .when(eventoService).delete(eventoId);

    // When & Then
    mockMvc.perform(delete("/api/eventos/" + eventoId))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("Evento não encontrado"));
  }
}

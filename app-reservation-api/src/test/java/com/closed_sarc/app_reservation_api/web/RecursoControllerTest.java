package com.closed_sarc.app_reservation_api.web;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

import com.closed_sarc.app_reservation_api.config.TestSecurityConfig;
import com.closed_sarc.app_reservation_api.domain.entities.Recurso;
import com.closed_sarc.app_reservation_api.service.RecursoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(RecursoController.class)
@TestPropertySource("classpath:application-test.properties")
@Import(TestSecurityConfig.class)
@DisplayName("RecursoController - Testes de Integração")
class RecursoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private RecursoService recursoService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("POST /api/recursos - Deve retornar 201 e criar recurso quando dados válidos")
  void deveRetornar201EcriarRecursoQuandoDadosValidos() throws Exception {
    // Given
    RecursoController.RecursoRequest request = new RecursoController.RecursoRequest(
        "Sala 401",
        "Sala de Aula",
        1,
        40);

    Recurso recursoCriado = Recurso.builder()
        .id(UUID.randomUUID())
        .nome("Sala 401")
        .tipo("Sala de Aula")
        .quantidade(1)
        .capacidade(40)
        .ativo(true)
        .build();

    when(recursoService.create(org.mockito.ArgumentMatchers.any(Recurso.class)))
        .thenReturn(recursoCriado);

    // When & Then
    mockMvc.perform(post("/api/recursos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.nome").value("Sala 401"))
        .andExpect(jsonPath("$.tipo").value("Sala de Aula"))
        .andExpect(jsonPath("$.quantidade").value(1))
        .andExpect(jsonPath("$.capacidade").value(40))
        .andExpect(jsonPath("$.ativo").value(true));
  }

  @Test
  @DisplayName("POST /api/recursos - Deve retornar 400 quando recurso já existe")
  void deveRetornar400QuandoRecursoJaExiste() throws Exception {
    // Given
    RecursoController.RecursoRequest request = new RecursoController.RecursoRequest(
        "Sala 401",
        "Sala de Aula",
        1,
        40);

    when(recursoService.create(org.mockito.ArgumentMatchers.any(Recurso.class)))
        .thenThrow(new IllegalArgumentException("Já existe um recurso ativo com este nome"));

    // When & Then
    mockMvc.perform(post("/api/recursos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("Já existe um recurso ativo com este nome"));
  }

  @Test
  @DisplayName("GET /api/recursos - Deve retornar 200 e lista de recursos")
  void deveRetornar200EListaRecursos() throws Exception {
    // Given
    Recurso recurso1 = Recurso.builder()
        .id(UUID.randomUUID())
        .nome("Sala 401")
        .tipo("Sala de Aula")
        .quantidade(1)
        .capacidade(40)
        .ativo(true)
        .build();

    Recurso recurso2 = Recurso.builder()
        .id(UUID.randomUUID())
        .nome("Projetor")
        .tipo("Equipamento")
        .quantidade(5)
        .capacidade(null)
        .ativo(true)
        .build();

    List<Recurso> recursos = Arrays.asList(recurso1, recurso2);
    when(recursoService.findAll()).thenReturn(recursos);

    // When & Then
    mockMvc.perform(get("/api/recursos")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].nome").value("Sala 401"))
        .andExpect(jsonPath("$[1].nome").value("Projetor"));
  }

  @Test
  @DisplayName("GET /api/recursos - Deve retornar 200 e lista vazia quando não há recursos")
  void deveRetornar200EListaVaziaQuandoNaoHaRecursos() throws Exception {
    // Given
    when(recursoService.findAll()).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(get("/api/recursos")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @DisplayName("DELETE /api/recursos/{id} - Deve retornar 204 quando recurso excluído com sucesso")
  void deveRetornar204QuandoRecursoExcluidoComSucesso() throws Exception {
    // Given
    UUID recursoId = UUID.randomUUID();
    doNothing().when(recursoService).delete(recursoId);

    // When & Then
    mockMvc.perform(delete("/api/recursos/" + recursoId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/recursos/{id} - Deve retornar 404 quando recurso não encontrado para exclusão")
  void deveRetornar404QuandoRecursoNaoEncontradoParaExclusao() throws Exception {
    // Given
    UUID recursoId = UUID.randomUUID();
    doThrow(new IllegalArgumentException("Recurso não encontrado"))
        .when(recursoService).delete(recursoId);

    // When & Then
    mockMvc.perform(delete("/api/recursos/" + recursoId))
        .andExpect(status().isNotFound());
  }
}

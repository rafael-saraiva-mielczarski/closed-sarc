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
import com.closed_sarc.app_registration_api.domain.entities.Disciplina;
import com.closed_sarc.app_registration_api.service.DisciplinaService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(DisciplinaController.class)
@TestPropertySource("classpath:application-test.properties")
@Import(TestSecurityConfig.class)
@DisplayName("DisciplinaController - Testes de Integração")
class DisciplinaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private DisciplinaService disciplinaService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("POST /api/disciplinas - Deve retornar 201 e criar disciplina quando dados válidos")
  void deveRetornar201EcriarDisciplinaQuandoDadosValidos() throws Exception {
    // Given
    DisciplinaController.DisciplinaRequest request = new DisciplinaController.DisciplinaRequest(
        "Programação Java",
        "Disciplina de programação orientada a objetos",
        60);

    Disciplina disciplinaCriada = Disciplina.builder()
        .id(UUID.randomUUID())
        .nome("Programação Java")
        .descricao("Disciplina de programação orientada a objetos")
        .cargaHoraria(60)
        .build();

    when(disciplinaService.create(org.mockito.ArgumentMatchers.any(Disciplina.class)))
        .thenReturn(disciplinaCriada);

    // When & Then
    mockMvc.perform(post("/api/disciplinas")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.nome").value("Programação Java"))
        .andExpect(jsonPath("$.descricao").value("Disciplina de programação orientada a objetos"))
        .andExpect(jsonPath("$.cargaHoraria").value(60));
  }

  @Test
  @DisplayName("POST /api/disciplinas - Deve retornar 400 quando dados inválidos")
  void deveRetornar400QuandoDadosInvalidos() throws Exception {
    // Given
    DisciplinaController.DisciplinaRequest request = new DisciplinaController.DisciplinaRequest(
        "Programação Java",
        "Disciplina de programação orientada a objetos",
        60);

    when(disciplinaService.create(org.mockito.ArgumentMatchers.any(Disciplina.class)))
        .thenThrow(new IllegalArgumentException("Nome é obrigatório"));

    // When & Then
    mockMvc.perform(post("/api/disciplinas")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("Nome é obrigatório"));
  }

  @Test
  @DisplayName("GET /api/disciplinas/{id} - Deve retornar 200 e disciplina quando ID válido")
  void deveRetornar200EDisciplinaQuandoIdValido() throws Exception {
    // Given
    UUID disciplinaId = UUID.randomUUID();
    Disciplina disciplina = Disciplina.builder()
        .id(disciplinaId)
        .nome("Programação Java")
        .descricao("Disciplina de programação orientada a objetos")
        .cargaHoraria(60)
        .build();

    when(disciplinaService.findById(disciplinaId)).thenReturn(disciplina);

    // When & Then
    mockMvc.perform(get("/api/disciplinas/" + disciplinaId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(disciplinaId.toString()))
        .andExpect(jsonPath("$.nome").value("Programação Java"))
        .andExpect(jsonPath("$.descricao").value("Disciplina de programação orientada a objetos"))
        .andExpect(jsonPath("$.cargaHoraria").value(60));
  }

  @Test
  @DisplayName("GET /api/disciplinas/{id} - Deve retornar 404 quando disciplina não encontrada")
  void deveRetornar404QuandoDisciplinaNaoEncontrada() throws Exception {
    // Given
    UUID disciplinaId = UUID.randomUUID();
    when(disciplinaService.findById(disciplinaId))
        .thenThrow(new IllegalArgumentException("Disciplina não encontrada"));

    // When & Then
    mockMvc.perform(get("/api/disciplinas/" + disciplinaId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET /api/disciplinas - Deve retornar 200 e lista de disciplinas")
  void deveRetornar200EListaDisciplinas() throws Exception {
    // Given
    Disciplina disciplina1 = Disciplina.builder()
        .id(UUID.randomUUID())
        .nome("Programação Java")
        .descricao("Disciplina de programação orientada a objetos")
        .cargaHoraria(60)
        .build();

    Disciplina disciplina2 = Disciplina.builder()
        .id(UUID.randomUUID())
        .nome("Banco de Dados")
        .descricao("Disciplina de banco de dados relacionais")
        .cargaHoraria(80)
        .build();

    List<Disciplina> disciplinas = Arrays.asList(disciplina1, disciplina2);
    when(disciplinaService.findAll()).thenReturn(disciplinas);

    // When & Then
    mockMvc.perform(get("/api/disciplinas")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].nome").value("Programação Java"))
        .andExpect(jsonPath("$[1].nome").value("Banco de Dados"));
  }

  @Test
  @DisplayName("GET /api/disciplinas - Deve retornar 200 e lista vazia quando não há disciplinas")
  void deveRetornar200EListaVaziaQuandoNaoHaDisciplinas() throws Exception {
    // Given
    when(disciplinaService.findAll()).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(get("/api/disciplinas")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @DisplayName("GET /api/disciplinas/nome/{nome} - Deve retornar 200 e disciplinas filtradas por nome")
  void deveRetornar200EDisciplinasFiltradasPorNome() throws Exception {
    // Given
    Disciplina disciplina = Disciplina.builder()
        .id(UUID.randomUUID())
        .nome("Programação Java")
        .descricao("Disciplina de programação orientada a objetos")
        .cargaHoraria(60)
        .build();

    List<Disciplina> disciplinas = Arrays.asList(disciplina);
    when(disciplinaService.findByNomeContaining("Programação")).thenReturn(disciplinas);

    // When & Then
    mockMvc.perform(get("/api/disciplinas/nome/Programação")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].nome").value("Programação Java"));
  }

  @Test
  @DisplayName("GET /api/disciplinas/nome/{nome} - Deve retornar 200 e lista vazia quando nenhuma disciplina encontrada")
  void deveRetornar200EListaVaziaQuandoNenhumaDisciplinaEncontrada() throws Exception {
    // Given
    when(disciplinaService.findByNomeContaining("Inexistente")).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(get("/api/disciplinas/nome/Inexistente")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @DisplayName("GET /api/disciplinas - Deve retornar 200 quando segurança desabilitada")
  void deveRetornar200QuandoSegurancaDesabilitada() throws Exception {
    // Given
    when(disciplinaService.findAll()).thenReturn(Arrays.asList());

    // When & Then
    mockMvc.perform(get("/api/disciplinas")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }
}

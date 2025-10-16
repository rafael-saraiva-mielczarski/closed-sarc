package com.closed_sarc.app_registration_api.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.closed_sarc.app_registration_api.application.dto.AulaDTO;
import com.closed_sarc.app_registration_api.application.dto.CronogramaDTO;
import com.closed_sarc.app_registration_api.application.service.CronogramaService;
import com.closed_sarc.app_registration_api.domain.entities.Horario;

@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource("classpath:application-test.properties")
@DisplayName("CronogramaController - Testes de Integração")
class CronogramaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CronogramaService cronogramaService;

  @Test
  @DisplayName("GET /api/cronograma - Deve retornar 200 e cronograma com aulas")
  void deveRetornar200EAulasQuandoExistemTurmas() throws Exception {
    // Given
    AulaDTO aula1 = AulaDTO.builder()
        .recurso("401")
        .nomeProfessor("Prof. Maria Silva")
        .nomeDisciplina("Programação Java")
        .turma("(33A)")
        .horario(Horario.A)
        .build();

    AulaDTO aula2 = AulaDTO.builder()
        .recurso("205")
        .nomeProfessor("Prof. Ana Costa")
        .nomeDisciplina("Desenvolvimento Web")
        .turma("(33C)")
        .horario(Horario.E)
        .build();

    CronogramaDTO cronogramaEsperado = CronogramaDTO.builder()
        .aulasDeHoje(Arrays.asList(aula1, aula2))
        .build();

    when(cronogramaService.consultarCronograma()).thenReturn(cronogramaEsperado);

    // When & Then
    mockMvc.perform(get("/api/cronograma")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.aulasDeHoje").isArray())
        .andExpect(jsonPath("$.aulasDeHoje.length()").value(2))
        .andExpect(jsonPath("$.aulasDeHoje[0].recurso").value("401"))
        .andExpect(jsonPath("$.aulasDeHoje[0].nomeProfessor").value("Prof. Maria Silva"))
        .andExpect(jsonPath("$.aulasDeHoje[0].nomeDisciplina").value("Programação Java"))
        .andExpect(jsonPath("$.aulasDeHoje[0].turma").value("(33A)"))
        .andExpect(jsonPath("$.aulasDeHoje[0].horario").value("A"))
        .andExpect(jsonPath("$.aulasDeHoje[1].recurso").value("205"))
        .andExpect(jsonPath("$.aulasDeHoje[1].nomeProfessor").value("Prof. Ana Costa"))
        .andExpect(jsonPath("$.aulasDeHoje[1].nomeDisciplina").value("Desenvolvimento Web"))
        .andExpect(jsonPath("$.aulasDeHoje[1].turma").value("(33C)"))
        .andExpect(jsonPath("$.aulasDeHoje[1].horario").value("E"));
  }

  @Test
  @DisplayName("GET /api/cronograma - Deve retornar 200 e lista vazia quando não há aulas")
  void deveRetornar200EListaVaziaQuandoNaoHaAulas() throws Exception {
    // Given
    CronogramaDTO cronogramaVazio = CronogramaDTO.builder()
        .aulasDeHoje(Collections.emptyList())
        .build();

    when(cronogramaService.consultarCronograma()).thenReturn(cronogramaVazio);

    // When & Then
    mockMvc.perform(get("/api/cronograma")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.aulasDeHoje").isArray())
        .andExpect(jsonPath("$.aulasDeHoje.length()").value(0));
  }

  @Test
  @DisplayName("GET /api/cronograma - Deve aceitar requisição sem headers especiais")
  void deveAceitarRequisicaoSemHeadersEspeciais() throws Exception {
    // Given
    CronogramaDTO cronogramaVazio = CronogramaDTO.builder()
        .aulasDeHoje(Collections.emptyList())
        .build();

    when(cronogramaService.consultarCronograma()).thenReturn(cronogramaVazio);

    // When & Then
    mockMvc.perform(get("/api/cronograma"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }
}

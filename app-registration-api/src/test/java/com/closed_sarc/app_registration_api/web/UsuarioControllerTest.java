package com.closed_sarc.app_registration_api.web;

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

import com.closed_sarc.app_registration_api.config.TestSecurityConfig;
import com.closed_sarc.app_registration_api.domain.entities.TipoUsuario;
import com.closed_sarc.app_registration_api.domain.entities.Usuario;
import com.closed_sarc.app_registration_api.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UsuarioController.class)
@TestPropertySource("classpath:application-test.properties")
@Import(TestSecurityConfig.class)
@DisplayName("UsuarioController - Testes de Integração")
class UsuarioControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UsuarioService usuarioService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("POST /api/usuarios - Deve retornar 201 e criar usuário quando dados válidos")
  void deveRetornar201EcriarUsuarioQuandoDadosValidos() throws Exception {
    // Given
    Usuario usuarioRequest = Usuario.builder()
        .nome("João Silva")
        .email("joao.silva@email.com")
        .senha("senha123")
        .tipo(TipoUsuario.ESTUDANTE)
        .build();

    Usuario usuarioCriado = Usuario.builder()
        .id(UUID.randomUUID())
        .nome("João Silva")
        .email("joao.silva@email.com")
        .senha("senha123")
        .tipo(TipoUsuario.ESTUDANTE)
        .build();

    when(usuarioService.create(org.mockito.ArgumentMatchers.any(Usuario.class)))
        .thenReturn(usuarioCriado);

    // When & Then
    mockMvc.perform(post("/api/usuarios")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(usuarioRequest)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.nome").value("João Silva"))
        .andExpect(jsonPath("$.email").value("joao.silva@email.com"));
  }

  @Test
  @DisplayName("POST /api/usuarios - Deve retornar 400 quando email já existe")
  void deveRetornar400QuandoEmailJaExiste() throws Exception {
    // Given
    Usuario usuarioRequest = Usuario.builder()
        .nome("João Silva")
        .email("joao.silva@email.com")
        .senha("senha123")
        .tipo(TipoUsuario.ESTUDANTE)
        .build();

    when(usuarioService.create(org.mockito.ArgumentMatchers.any(Usuario.class)))
        .thenThrow(new IllegalArgumentException("Email já cadastrado"));

    // When & Then
    mockMvc.perform(post("/api/usuarios")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(usuarioRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("Email já cadastrado"));
  }

  @Test
  @DisplayName("GET /api/usuarios/{id} - Deve retornar 200 e usuário quando ID válido")
  void deveRetornar200EUsuarioQuandoIdValido() throws Exception {
    // Given
    UUID usuarioId = UUID.randomUUID();
    Usuario usuario = Usuario.builder()
        .id(usuarioId)
        .nome("João Silva")
        .email("joao.silva@email.com")
        .senha("senha123")
        .tipo(TipoUsuario.ESTUDANTE)
        .build();

    when(usuarioService.findById(usuarioId)).thenReturn(usuario);

    // When & Then
    mockMvc.perform(get("/api/usuarios/" + usuarioId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(usuarioId.toString()))
        .andExpect(jsonPath("$.nome").value("João Silva"))
        .andExpect(jsonPath("$.email").value("joao.silva@email.com"));
  }

  @Test
  @DisplayName("GET /api/usuarios/{id} - Deve retornar 404 quando usuário não encontrado")
  void deveRetornar404QuandoUsuarioNaoEncontrado() throws Exception {
    // Given
    UUID usuarioId = UUID.randomUUID();
    when(usuarioService.findById(usuarioId))
        .thenThrow(new IllegalArgumentException("Usuário não encontrado"));

    // When & Then
    mockMvc.perform(get("/api/usuarios/" + usuarioId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET /api/usuarios - Deve retornar 200 e lista de usuários")
  void deveRetornar200EListaUsuarios() throws Exception {
    // Given
    Usuario usuario1 = Usuario.builder()
        .id(UUID.randomUUID())
        .nome("João Silva")
        .email("joao.silva@email.com")
        .senha("senha123")
        .tipo(TipoUsuario.ESTUDANTE)
        .build();

    Usuario usuario2 = Usuario.builder()
        .id(UUID.randomUUID())
        .nome("Maria Santos")
        .email("maria.santos@email.com")
        .senha("senha456")
        .tipo(TipoUsuario.PROFESSOR)
        .build();

    List<Usuario> usuarios = Arrays.asList(usuario1, usuario2);
    when(usuarioService.findAll()).thenReturn(usuarios);

    // When & Then
    mockMvc.perform(get("/api/usuarios")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].nome").value("João Silva"))
        .andExpect(jsonPath("$[1].nome").value("Maria Santos"));
  }

  @Test
  @DisplayName("GET /api/usuarios - Deve retornar 200 e lista vazia quando não há usuários")
  void deveRetornar200EListaVaziaQuandoNaoHaUsuarios() throws Exception {
    // Given
    when(usuarioService.findAll()).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(get("/api/usuarios")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @DisplayName("DELETE /api/usuarios/{id} - Deve retornar 204 quando usuário excluído com sucesso")
  void deveRetornar204QuandoUsuarioExcluidoComSucesso() throws Exception {
    // Given
    UUID usuarioId = UUID.randomUUID();
    doNothing().when(usuarioService).deleteById(usuarioId);

    // When & Then
    mockMvc.perform(delete("/api/usuarios/" + usuarioId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/usuarios/{id} - Deve retornar 404 quando usuário não encontrado para exclusão")
  void deveRetornar404QuandoUsuarioNaoEncontradoParaExclusao() throws Exception {
    // Given
    UUID usuarioId = UUID.randomUUID();
    doThrow(new IllegalArgumentException("Usuário não encontrado"))
        .when(usuarioService).deleteById(usuarioId);

    // When & Then
    mockMvc.perform(delete("/api/usuarios/" + usuarioId))
        .andExpect(status().isNotFound());
  }
}

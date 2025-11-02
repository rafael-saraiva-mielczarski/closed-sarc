package com.closed_sarc.app_registration_api.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.closed_sarc.app_registration_api.application.dto.CronogramaDTO;
import com.closed_sarc.app_registration_api.application.service.CronogramaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cronograma")
@RequiredArgsConstructor
@Tag(name = "Cronograma", description = "API para consulta de cronograma de aulas")
public class CronogramaController {

  private final CronogramaService cronogramaService;

  @GetMapping()
  @Operation(summary = "Consultar cronograma do dia atual", description = "Retorna todas as aulas e eventos programados para o dia atual. "
      +
      "Este endpoint é público e não requer autenticação. " +
      "O dia da semana é detectado automaticamente com base na data atual do sistema.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cronograma consultado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CronogramaDTO.class), examples = {
          @ExampleObject(name = "Com aulas e eventos", description = "Exemplo quando há aulas e eventos programados para hoje", value = """
              {
                "aulasDeHoje": [
                  {
                    "turmaId": "550e8400-e29b-41d4-a716-446655440000",
                    "nomeProfessor": "Prof. Maria Silva",
                    "nomeDisciplina": "Programação Java",
                    "turma": "(33A)",
                    "horario": "A",
                    "recursosReservados": []
                  },
                  {
                    "turmaId": "660e8400-e29b-41d4-a716-446655440001",
                    "nomeProfessor": "Prof. Ana Costa",
                    "nomeDisciplina": "Desenvolvimento Web",
                    "turma": "(33C)",
                    "horario": "E",
                    "recursosReservados": []
                  }
                ],
                "eventosDeHoje": [
                  {
                    "titulo": "Reunião de planejamento",
                    "descricao": "Reunião para planejamento das próximas atividades",
                    "dataInicio": "2024-10-16T08:00:00Z",
                    "dataFim": "2024-10-16T09:30:00Z"
                  }
                ]
              }
              """),
          @ExampleObject(name = "Sem aulas", description = "Exemplo quando não há aulas nem eventos para hoje", value = """
              {
                "aulasDeHoje": [],
                "eventosDeHoje": []
              }
              """)
      })),
      @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2025-10-16T10:30:00",
            "status": 500,
            "error": "Internal Server Error",
            "path": "/api/cronograma"
          }
          """)))
  })
  public ResponseEntity<CronogramaDTO> consultarCronograma() {
    CronogramaDTO cronograma = cronogramaService.consultarCronograma();
    return ResponseEntity.ok(cronograma);
  }
}

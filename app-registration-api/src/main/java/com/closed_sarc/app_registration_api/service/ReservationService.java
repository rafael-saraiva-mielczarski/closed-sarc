package com.closed_sarc.app_registration_api.service;

import com.closed_sarc.app_registration_api.infrastructure.client.dto.ReservaResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ReservationService {

    @Value("${reservation-api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public ReservaResponseDTO createReservation(UUID turmaId, UUID recursoId, Integer quantidade, Instant dataUso) {
        try {
            log.info("Criando reserva: turmaId={}, recursoId={}, quantidade={} - URL: {}",
                    turmaId, recursoId, quantidade, baseUrl + "/api/reservas");

            ReservationRequest request = new ReservationRequest(turmaId, recursoId, quantidade, dataUso);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString("master@reservation.com:master123".getBytes()));

            HttpEntity<ReservationRequest> httpEntity = new HttpEntity<>(request, headers);

            ResponseEntity<ReservaResponseDTO> response = restTemplate.postForEntity(
                    baseUrl + "/api/reservas",
                    httpEntity,
                    ReservaResponseDTO.class
            );

            log.info("Reserva criada com sucesso: {}", response.getBody().getId());
            return response.getBody();

        } catch (Exception e) {
            log.error("Erro ao criar reserva", e);
            throw new RuntimeException("Erro ao criar reserva: " + e.getMessage(), e);
        }
    }

    public List<ReservaResponseDTO> getReservationsByTurmaAndData(UUID turmaId, Instant dataUso) {
        try {
            log.info("Buscando reservas: turmaId={}, dataUso={} - URL: {}",
                    turmaId, dataUso, baseUrl + "/api/reservas/turma/" + turmaId + "/data");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString("master@reservation.com:master123".getBytes()));

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            String url = baseUrl + "/api/reservas/turma/" + turmaId + "/data?dataUso=" + dataUso.toString();

            ResponseEntity<List<ReservaResponseDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    httpEntity,
                    new ParameterizedTypeReference<List<ReservaResponseDTO>>() {}
            );

            List<ReservaResponseDTO> reservas = response.getBody() != null ? response.getBody() : Collections.emptyList();
            log.info("Encontradas {} reservas", reservas.size());
            return reservas;

        } catch (Exception e) {
            log.error("Erro ao buscar reservas por turma e data", e);
            return Collections.emptyList();
        }
    }

    public List<ReservaResponseDTO> getReservationsByTurma(UUID turmaId) {
        try {
            log.info("Buscando reservas por turma: turmaId={} - URL: {}",
                    turmaId, baseUrl + "/api/reservas/turma/" + turmaId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString("master@reservation.com:master123".getBytes()));

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            ResponseEntity<List<ReservaResponseDTO>> response = restTemplate.exchange(
                    baseUrl + "/api/reservas/turma/" + turmaId,
                    HttpMethod.GET,
                    httpEntity,
                    new ParameterizedTypeReference<List<ReservaResponseDTO>>() {}
            );

            List<ReservaResponseDTO> reservas = response.getBody() != null ? response.getBody() : Collections.emptyList();
            log.info("Encontradas {} reservas", reservas.size());
            return reservas;

        } catch (Exception e) {
            log.error("Erro ao buscar reservas por turma", e);
            throw new RuntimeException("Erro ao buscar reservas por turma: " + e.getMessage(), e);
        }
    }

    private static class ReservationRequest {
        private UUID turmaId;
        private UUID recursoId;
        private Integer quantidade;
        private Instant dataUso;

        public ReservationRequest(UUID turmaId, UUID recursoId, Integer quantidade, Instant dataUso) {
            this.turmaId = turmaId;
            this.recursoId = recursoId;
            this.quantidade = quantidade;
            this.dataUso = dataUso;
        }

        public UUID getTurmaId() {
            return turmaId;
        }

        public UUID getRecursoId() {
            return recursoId;
        }

        public Integer getQuantidade() {
            return quantidade;
        }

        public Instant getDataUso() {
            return dataUso;
        }
    }
}


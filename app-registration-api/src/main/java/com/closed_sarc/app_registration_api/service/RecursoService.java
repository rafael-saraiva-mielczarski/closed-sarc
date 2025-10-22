package com.closed_sarc.app_registration_api.service;

import com.closed_sarc.app_registration_api.application.dto.RecursoDTO;
import com.closed_sarc.app_registration_api.application.dto.RecursoRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecursoService {
    
    private final WebClient reservationApiWebClient;

    public RecursoDTO create(RecursoRequestDTO recursoRequest) {
        try {
            log.info("Criando recurso: {}", recursoRequest.getNome());
            
            RecursoDTO response = reservationApiWebClient
                    .post()
                    .uri("/api/recursos")
                    .bodyValue(recursoRequest)
                    .retrieve()
                    .bodyToMono(RecursoDTO.class)
                    .block();
            
            log.info("Recurso criado com sucesso: {}", response.getId());
            return response;
            
        } catch (WebClientResponseException e) {
            log.error("Erro ao criar recurso: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Erro ao criar recurso: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Erro inesperado ao criar recurso", e);
            throw new RuntimeException("Erro inesperado ao criar recurso", e);
        }
    }

    public List<RecursoDTO> findAll() {
        try {
            log.info("Buscando todos os recursos");
            
            List<RecursoDTO> recursos = reservationApiWebClient
                    .get()
                    .uri("/api/recursos")
                    .retrieve()
                    .bodyToFlux(RecursoDTO.class)
                    .collectList()
                    .block();
            
            log.info("Encontrados {} recursos", recursos != null ? recursos.size() : 0);
            return recursos != null ? recursos : List.of();
            
        } catch (WebClientResponseException e) {
            log.error("Erro ao buscar recursos: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Erro ao buscar recursos: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar recursos", e);
            throw new RuntimeException("Erro inesperado ao buscar recursos", e);
        }
    }

    public void delete(UUID id) {
        try {
            log.info("Excluindo recurso: {}", id);
            
            reservationApiWebClient
                    .delete()
                    .uri("/api/recursos/{id}", id)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            
            log.info("Recurso excluído com sucesso: {}", id);
            
        } catch (WebClientResponseException e) {
            log.error("Erro ao excluir recurso: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Erro ao excluir recurso: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Erro inesperado ao excluir recurso", e);
            throw new RuntimeException("Erro inesperado ao excluir recurso", e);
        }
    }

    public RecursoDTO findById(UUID id) {
        try {
            log.info("Buscando recurso por ID: {}", id);
            
            // Como não temos endpoint específico por ID na reservation-api,
            // vamos buscar todos e filtrar
            List<RecursoDTO> recursos = findAll();
            
            return recursos.stream()
                    .filter(recurso -> recurso.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Recurso não encontrado"));
            
        } catch (Exception e) {
            log.error("Erro ao buscar recurso por ID: {}", id, e);
            throw new RuntimeException("Erro ao buscar recurso por ID", e);
        }
    }
}

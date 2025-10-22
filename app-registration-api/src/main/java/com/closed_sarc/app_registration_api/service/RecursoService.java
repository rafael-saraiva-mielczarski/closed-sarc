package com.closed_sarc.app_registration_api.service;

import com.closed_sarc.app_registration_api.application.dto.RecursoDTO;
import com.closed_sarc.app_registration_api.application.dto.RecursoRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class RecursoService {
    
    @Value("${reservation-api.base-url}")
    private String baseUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();

    public RecursoDTO create(RecursoRequestDTO recursoRequest) {
        try {
            log.info("Criando recurso: {} - URL: {}", recursoRequest.getNome(), baseUrl + "/api/recursos");
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            
            HttpEntity<RecursoRequestDTO> request = new HttpEntity<>(recursoRequest, headers);
            
            ResponseEntity<RecursoDTO> response = restTemplate.postForEntity(
                baseUrl + "/api/recursos", 
                request, 
                RecursoDTO.class
            );
            
            log.info("Recurso criado com sucesso: {}", response.getBody().getId());
            return response.getBody();
            
        } catch (Exception e) {
            log.error("Erro ao criar recurso", e);
            throw new RuntimeException("Erro ao criar recurso: " + e.getMessage());
        }
    }

    public List<RecursoDTO> findAll() {
        try {
            log.info("Buscando todos os recursos - URL: {}", baseUrl + "/api/recursos");
            
            ResponseEntity<RecursoDTO[]> response = restTemplate.getForEntity(
                baseUrl + "/api/recursos", 
                RecursoDTO[].class
            );
            
            List<RecursoDTO> recursos = Arrays.asList(response.getBody());
            log.info("Encontrados {} recursos", recursos.size());
            return recursos;
            
        } catch (Exception e) {
            log.error("Erro ao buscar recursos", e);
            throw new RuntimeException("Erro ao buscar recursos: " + e.getMessage());
        }
    }

    public void delete(UUID id) {
        try {
            log.info("Excluindo recurso: {} - URL: {}", id, baseUrl + "/api/recursos/" + id);
            
            restTemplate.delete(baseUrl + "/api/recursos/" + id);
            
            log.info("Recurso excluído com sucesso: {}", id);
            
        } catch (Exception e) {
            log.error("Erro ao excluir recurso", e);
            throw new RuntimeException("Erro ao excluir recurso: " + e.getMessage());
        }
    }

    public RecursoDTO findById(UUID id) {
        try {
            log.info("Buscando recurso por ID: {} - URL: {}", id, baseUrl + "/api/recursos");
            
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

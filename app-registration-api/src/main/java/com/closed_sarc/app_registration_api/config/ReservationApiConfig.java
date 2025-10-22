package com.closed_sarc.app_registration_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ReservationApiConfig {

    @Value("${reservation-api.base-url}")
    private String baseUrl;

    @Value("${reservation-api.username}")
    private String username;

    @Value("${reservation-api.password}")
    private String password;

    @Bean
    public WebClient reservationApiWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(headers -> {
                    String auth = username + ":" + password;
                    String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes());
                    headers.set("Authorization", "Basic " + encodedAuth);
                    headers.set("Content-Type", "application/json");
                })
                .build();
    }
}

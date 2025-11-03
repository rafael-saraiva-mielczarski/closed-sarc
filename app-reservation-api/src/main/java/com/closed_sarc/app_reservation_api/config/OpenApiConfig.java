package com.closed_sarc.app_reservation_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ClosedSARC - App Reservation API")
                        .description("API para reservas de recursos e salas. " +
                                "Inspirada no sistema OpenSARC da PUCRS. ")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe ClosedSARC")
                                .email("suporte@closed-sarc.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addServersItem(new Server()
                        .url("http://localhost:8082")
                        .description("Servidor de Desenvolvimento"));
    }
}

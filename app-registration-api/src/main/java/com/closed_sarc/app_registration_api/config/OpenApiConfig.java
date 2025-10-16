package com.closed_sarc.app_registration_api.config;

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
            .title("ClosedSARC - App Registration API")
            .description("API para consulta de cronograma de aulas e gerenciamento de usuários. " +
                "Inspirada no sistema OpenSARC da PUCRS. " +
                "O endpoint de cronograma é público e permite consultar aulas do dia atual.")
            .version("1.0.0")
            .contact(new Contact()
                .name("Equipe ClosedSARC")
                .email("suporte@closed-sarc.com"))
            .license(new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT")))
        .addServersItem(new Server()
            .url("http://localhost:8081")
            .description("Servidor de Desenvolvimento"));
  }
}

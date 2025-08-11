package com.supplychainrisk.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smart Supply Chain Risk Intelligence API")
                        .description("RESTful API for managing supply chain operations, tracking shipments, and assessing risks")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Supply Chain Team")
                                .email("support@supplychainrisk.com")
                                .url("https://supplychainrisk.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development server"),
                        new Server()
                                .url("https://api.supplychainrisk.com")
                                .description("Production server")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token for authentication"))
                        .addSecuritySchemes("firebase-token", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("Firebase")
                                .description("Firebase authentication token")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearer-jwt"))
                .addSecurityItem(new SecurityRequirement()
                        .addList("firebase-token"));
    }
}
package com.supplychain.risk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main Spring Boot application class for Smart Supply Chain Risk Intelligence platform.
 * 
 * This application provides Firebase authentication backend with role-based access control
 * for managing supply chain risk intelligence data.
 * 
 * @author Supply Chain Risk Intelligence Team
 * @version 1.0
 * @since 2024-01-01
 */
@SpringBootApplication
@EnableJpaAuditing
public class SupplyChainRiskApplication {

    /**
     * Main method to bootstrap the Spring Boot application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(SupplyChainRiskApplication.class, args);
    }
}
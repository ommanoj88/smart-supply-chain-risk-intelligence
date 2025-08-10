package com.supplychain.risk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SupplyChainRiskApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplyChainRiskApplication.class, args);
    }
}
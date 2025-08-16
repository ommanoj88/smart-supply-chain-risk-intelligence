package com.supplychainrisk.controller;

import com.supplychainrisk.service.DataSeedingService;
import com.supplychainrisk.service.EnterpriseTestingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    @Autowired
    private DataSeedingService dataSeedingService;
    
    @Autowired
    private EnterpriseTestingService enterpriseTestingService;
    
    @PostMapping("/seed-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> seedData() {
        try {
            logger.info("Starting data seeding process...");
            dataSeedingService.seedAllData();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Data seeding completed successfully"
            ));
            
        } catch (Exception e) {
            logger.error("Data seeding failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Data seeding failed: " + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/data-stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUDITOR')")
    public ResponseEntity<?> getDataStats() {
        try {
            Map<String, Object> stats = enterpriseTestingService.getSystemStats();
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("Error retrieving data stats: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to retrieve data statistics"
            ));
        }
    }
    
    // Enterprise Testing Environment Endpoints
    
    @PostMapping("/testing/generate-scenario")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateTestingScenario(@RequestBody Map<String, Object> scenarioConfig) {
        try {
            logger.info("Generating testing scenario: {}", scenarioConfig.get("scenarioType"));
            Map<String, Object> result = enterpriseTestingService.generateTestingScenario(scenarioConfig);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "scenario", result,
                "message", "Testing scenario generated successfully"
            ));
            
        } catch (Exception e) {
            logger.error("Scenario generation failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Scenario generation failed: " + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/testing/simulate-crisis")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> simulateCrisis(@RequestBody Map<String, Object> crisisConfig) {
        try {
            logger.info("Simulating crisis scenario: {}", crisisConfig.get("crisisType"));
            Map<String, Object> result = enterpriseTestingService.simulateCrisisScenario(crisisConfig);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "simulation", result,
                "message", "Crisis simulation started successfully"
            ));
            
        } catch (Exception e) {
            logger.error("Crisis simulation failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Crisis simulation failed: " + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/testing/load-test-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateLoadTestData(@RequestBody Map<String, Object> loadConfig) {
        try {
            logger.info("Generating load test data with config: {}", loadConfig);
            Map<String, Object> result = enterpriseTestingService.generateLoadTestData(loadConfig);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", result,
                "message", "Load test data generated successfully"
            ));
            
        } catch (Exception e) {
            logger.error("Load test data generation failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Load test data generation failed: " + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/testing/scenarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAvailableScenarios() {
        try {
            Map<String, Object> scenarios = enterpriseTestingService.getAvailableTestingScenarios();
            return ResponseEntity.ok(scenarios);
            
        } catch (Exception e) {
            logger.error("Error retrieving scenarios: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to retrieve available scenarios"
            ));
        }
    }
    
    @PostMapping("/testing/reset-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resetTestingData() {
        try {
            logger.info("Resetting testing data...");
            enterpriseTestingService.resetAllTestingData();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Testing data reset successfully"
            ));
            
        } catch (Exception e) {
            logger.error("Data reset failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Data reset failed: " + e.getMessage()
            ));
        }
    }
}
package com.supplychainrisk.controller;

import com.supplychainrisk.dto.CrisisScenarioRequest;
import com.supplychainrisk.dto.MarketDataRequest;
import com.supplychainrisk.dto.SupplierScenarioRequest;
import com.supplychainrisk.service.DataSeedingService;
import com.supplychainrisk.service.EnterpriseTestingService;
import com.supplychainrisk.service.EnhancedMockDataService;
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
    
    @Autowired
    private EnhancedMockDataService enhancedMockDataService;
    
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
    
    // ===== Enterprise Testing Environment Endpoints =====
    
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
    
    @PostMapping("/testing/generate-mock-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateMockData(@RequestBody Map<String, Object> dataConfig) {
        try {
            String dataType = (String) dataConfig.get("dataType");
            logger.info("Generating mock data for type: {}", dataType);
            
            Map<String, Object> result = enterpriseTestingService.generateMockData(dataConfig);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", result,
                "message", "Mock data generated successfully"
            ));
            
        } catch (Exception e) {
            logger.error("Mock data generation failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Mock data generation failed: " + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/testing/clear-mock-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> clearMockData() {
        try {
            logger.info("Clearing mock data...");
            enterpriseTestingService.clearMockData();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Mock data cleared successfully"
            ));
            
        } catch (Exception e) {
            logger.error("Clear mock data failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Clear mock data failed: " + e.getMessage()
            ));
        }
    }
    
    // ===== Enhanced Mock Data Generation Endpoints (PR #3) =====
    
    @PostMapping("/mock-data/crisis-scenario")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateCrisisScenario(@RequestBody CrisisScenarioRequest request) {
        try {
            logger.info("Generating enhanced crisis scenario: {}", request.getScenarioType());
            Map<String, Object> result = enhancedMockDataService.generateCrisisScenario(request);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "scenario", result,
                "message", "Crisis scenario generated successfully"
            ));
            
        } catch (Exception e) {
            logger.error("Crisis scenario generation failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Crisis scenario generation failed: " + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/mock-data/market-scenario")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateMarketScenario(@RequestBody MarketDataRequest request) {
        try {
            logger.info("Generating market data scenario: {}", request.getDataType());
            Map<String, Object> result = enhancedMockDataService.generateMarketDataScenario(request);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "marketData", result,
                "message", "Market data scenario generated successfully"
            ));
            
        } catch (Exception e) {
            logger.error("Market scenario generation failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Market scenario generation failed: " + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/mock-data/supplier-scenario")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateSupplierScenario(@RequestBody SupplierScenarioRequest request) {
        try {
            logger.info("Generating supplier performance scenario: {} for supplier {}", 
                request.getScenarioType(), request.getSupplierId());
            Map<String, Object> result = enhancedMockDataService.generateSupplierPerformanceScenario(request);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "supplierScenario", result,
                "message", "Supplier scenario generated successfully"
            ));
            
        } catch (Exception e) {
            logger.error("Supplier scenario generation failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Supplier scenario generation failed: " + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/mock-data/complex-shipment-scenario")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateComplexShipmentScenario(@RequestBody Map<String, Object> request) {
        try {
            logger.info("Generating complex shipment scenario: {}", request.get("scenarioType"));
            
            // Generate complex shipment scenarios (multi-modal, customs delays, port congestion)
            Map<String, Object> result = generateComplexShipmentData(request);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "shipmentScenario", result,
                "message", "Complex shipment scenario generated successfully"
            ));
            
        } catch (Exception e) {
            logger.error("Complex shipment scenario generation failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Complex shipment scenario generation failed: " + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/mock-data/scenario-capabilities")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<?> getScenarioCapabilities() {
        try {
            Map<String, Object> capabilities = Map.of(
                "crisisScenarios", Map.of(
                    "available", new String[]{"HURRICANE", "TRADE_WAR", "SUPPLIER_BANKRUPTCY", "PANDEMIC"},
                    "description", "Comprehensive crisis scenarios with cascading effects"
                ),
                "marketDataScenarios", Map.of(
                    "available", new String[]{"CURRENCY", "COMMODITY", "WEATHER", "ECONOMIC"},
                    "description", "Market data simulation affecting costs and routing"
                ),
                "supplierScenarios", Map.of(
                    "available", new String[]{"PERFORMANCE_DEGRADATION", "CAPACITY_CONSTRAINT", "QUALITY_ISSUE", "COMPLIANCE_ISSUE"},
                    "description", "Advanced supplier performance scenarios"
                ),
                "shipmentScenarios", Map.of(
                    "available", new String[]{"MULTI_MODAL_TRANSPORT", "CUSTOMS_DELAYS", "PORT_CONGESTION", "ROUTE_OPTIMIZATION"},
                    "description", "Complex shipment scenarios with realistic delays"
                ),
                "features", new String[]{
                    "Realistic crisis scenarios with 18-month historical data",
                    "Market data integration with currency and commodity impacts",
                    "Advanced supplier degradation patterns",
                    "Multi-modal transport simulation",
                    "Cascading effect modeling",
                    "Recovery timeline simulation"
                }
            );
            
            return ResponseEntity.ok(capabilities);
            
        } catch (Exception e) {
            logger.error("Error retrieving scenario capabilities: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to retrieve scenario capabilities"
            ));
        }
    }
    
    // ===== Helper Methods for Complex Scenarios =====
    
    private Map<String, Object> generateComplexShipmentData(Map<String, Object> request) {
        String scenarioType = (String) request.get("scenarioType");
        
        return switch (scenarioType) {
            case "MULTI_MODAL_TRANSPORT" -> generateMultiModalTransportScenario();
            case "CUSTOMS_DELAYS" -> generateCustomsDelayScenario();
            case "PORT_CONGESTION" -> generatePortCongestionScenario();
            case "ROUTE_OPTIMIZATION" -> generateRouteOptimizationScenario();
            default -> Map.of("error", "Unknown shipment scenario type");
        };
    }
    
    private Map<String, Object> generateMultiModalTransportScenario() {
        return Map.of(
            "scenarioType", "MULTI_MODAL_TRANSPORT",
            "transportModes", new String[]{"OCEAN", "RAIL", "TRUCK"},
            "intermodalDelays", "2-4 hours at each transfer point",
            "costOptimization", "15% savings vs air freight",
            "timeTradeoff", "+5 days vs direct air transport",
            "transferPoints", new String[]{"Port of Long Beach", "Chicago Rail Hub", "Final Mile Truck"},
            "generatedAt", java.time.LocalDateTime.now()
        );
    }
    
    private Map<String, Object> generateCustomsDelayScenario() {
        return Map.of(
            "scenarioType", "CUSTOMS_DELAYS",
            "averageDelayHours", 24,
            "documentationIssues", "Missing commercial invoice details",
            "dutyChanges", "+12% unexpected tariff adjustment",
            "inspectionRate", "25% physical inspection rate",
            "borderCongestion", "HIGH - 48 hour queue at border",
            "generatedAt", java.time.LocalDateTime.now()
        );
    }
    
    private Map<String, Object> generatePortCongestionScenario() {
        return Map.of(
            "scenarioType", "PORT_CONGESTION",
            "affectedPorts", new String[]{"Los Angeles", "Long Beach", "Rotterdam", "Singapore"},
            "vesselWaitingTime", "5-8 days average",
            "containerShortage", "CRITICAL - 40% shortage",
            "alternativePorts", new String[]{"Oakland", "Seattle", "Hamburg"},
            "costIncrease", "25-40% premium for expedited handling",
            "generatedAt", java.time.LocalDateTime.now()
        );
    }
    
    private Map<String, Object> generateRouteOptimizationScenario() {
        return Map.of(
            "scenarioType", "ROUTE_OPTIMIZATION",
            "emergencyRerouting", "Suez Canal blockage - Panama alternative",
            "costOptimization", "Northern route saves 15% on fuel",
            "timeOptimization", "Air freight reduces time by 12 days",
            "riskMitigation", "Avoiding high-piracy areas adds 2 days but reduces insurance cost",
            "generatedAt", java.time.LocalDateTime.now()
        );
    }
}
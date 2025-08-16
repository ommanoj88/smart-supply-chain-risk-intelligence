package com.supplychainrisk.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Enterprise-grade API simulator for external system integration testing
 * Simulates responses from major enterprise systems like SAP, Oracle, 
 * weather APIs, financial systems, and government services
 */
@RestController
@RequestMapping("/mock-apis/enterprise")
@CrossOrigin(origins = "http://localhost:3000")
public class EnterpriseAPISimulatorController {
    
    private static final Logger logger = LoggerFactory.getLogger(EnterpriseAPISimulatorController.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
    // SAP API Simulations
    
    @GetMapping("/sap/digital-manufacturing/orders")
    public ResponseEntity<?> getSAPManufacturingOrders() {
        logger.info("SAP Digital Manufacturing orders API request");
        
        Map<String, Object> response = new HashMap<>();
        response.put("systemId", "SAP_DM_PROD");
        response.put("timestamp", LocalDateTime.now().format(FORMATTER));
        
        List<Map<String, Object>> orders = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            orders.add(Map.of(
                "orderId", "MO-2024-" + String.format("%06d", i),
                "materialNumber", "MAT-" + (1000 + i),
                "quantity", ThreadLocalRandom.current().nextInt(100, 1000),
                "plannedStartDate", LocalDateTime.now().plusDays(i).format(FORMATTER),
                "status", getRandomStatus(Arrays.asList("CREATED", "RELEASED", "IN_PRODUCTION", "COMPLETED")),
                "priority", getRandomStatus(Arrays.asList("LOW", "MEDIUM", "HIGH", "CRITICAL")),
                "plant", "PLT-" + (i % 3 + 1),
                "workCenter", "WC-" + (i % 5 + 1)
            ));
        }
        
        response.put("orders", orders);
        response.put("totalCount", orders.size());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/sap/ariba/suppliers")
    public ResponseEntity<?> getSAPAribaSuppliers() {
        logger.info("SAP Ariba suppliers API request");
        
        Map<String, Object> response = new HashMap<>();
        response.put("systemId", "SAP_ARIBA_PROD");
        response.put("timestamp", LocalDateTime.now().format(FORMATTER));
        
        List<Map<String, Object>> suppliers = new ArrayList<>();
        String[] categories = {"Electronics", "Automotive", "Chemicals", "Textiles", "Machinery"};
        String[] countries = {"Germany", "China", "United States", "Japan", "South Korea"};
        
        for (int i = 1; i <= 20; i++) {
            suppliers.add(Map.of(
                "supplierId", "ARB-SUP-" + String.format("%05d", i),
                "companyName", "Ariba Supplier " + i,
                "category", getRandomStatus(Arrays.asList(categories)),
                "country", getRandomStatus(Arrays.asList(countries)),
                "certificationLevel", getRandomStatus(Arrays.asList("GOLD", "SILVER", "BRONZE", "STANDARD")),
                "riskRating", ThreadLocalRandom.current().nextDouble(1.0, 5.0),
                "onTimeDeliveryRate", ThreadLocalRandom.current().nextDouble(0.85, 0.99),
                "qualityScore", ThreadLocalRandom.current().nextDouble(0.90, 1.0),
                "lastAssessment", LocalDateTime.now().minusDays(ThreadLocalRandom.current().nextInt(1, 90)).format(FORMATTER)
            ));
        }
        
        response.put("suppliers", suppliers);
        response.put("totalCount", suppliers.size());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/sap/ibp/demand-forecast")
    public ResponseEntity<?> getSAPIBPDemandForecast(@RequestParam(required = false) String material) {
        logger.info("SAP IBP demand forecast API request for material: {}", material);
        
        Map<String, Object> response = new HashMap<>();
        response.put("systemId", "SAP_IBP_PROD");
        response.put("timestamp", LocalDateTime.now().format(FORMATTER));
        response.put("materialNumber", material != null ? material : "MAT-DEFAULT");
        
        List<Map<String, Object>> forecast = new ArrayList<>();
        for (int week = 1; week <= 12; week++) {
            LocalDateTime weekStart = LocalDateTime.now().plusWeeks(week);
            forecast.add(Map.of(
                "weekStarting", weekStart.format(FORMATTER),
                "forecastQuantity", ThreadLocalRandom.current().nextInt(500, 2000),
                "confidenceLevel", ThreadLocalRandom.current().nextDouble(0.70, 0.95),
                "forecastModel", "ML_ENSEMBLE_V2",
                "lastUpdated", LocalDateTime.now().minusHours(ThreadLocalRandom.current().nextInt(1, 24)).format(FORMATTER)
            ));
        }
        
        response.put("forecast", forecast);
        response.put("forecastHorizon", "12 weeks");
        response.put("accuracy", ThreadLocalRandom.current().nextDouble(0.80, 0.95));
        
        return ResponseEntity.ok(response);
    }
    
    // Oracle ERP Simulations
    
    @GetMapping("/oracle/erp/financials/invoices")
    public ResponseEntity<?> getOracleERPInvoices() {
        logger.info("Oracle ERP financials API request");
        
        Map<String, Object> response = new HashMap<>();
        response.put("systemId", "ORACLE_ERP_PROD");
        response.put("timestamp", LocalDateTime.now().format(FORMATTER));
        
        List<Map<String, Object>> invoices = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            invoices.add(Map.of(
                "invoiceNumber", "INV-2024-" + String.format("%08d", i),
                "supplierNumber", "SUP-" + String.format("%05d", i),
                "invoiceDate", LocalDateTime.now().minusDays(ThreadLocalRandom.current().nextInt(1, 30)).format(FORMATTER),
                "amount", ThreadLocalRandom.current().nextDouble(1000, 50000),
                "currency", getRandomStatus(Arrays.asList("USD", "EUR", "GBP", "JPY", "CNY")),
                "status", getRandomStatus(Arrays.asList("PENDING", "APPROVED", "PAID", "DISPUTED")),
                "paymentTerms", getRandomStatus(Arrays.asList("NET30", "NET45", "NET60", "COD")),
                "dueDate", LocalDateTime.now().plusDays(ThreadLocalRandom.current().nextInt(15, 60)).format(FORMATTER)
            ));
        }
        
        response.put("invoices", invoices);
        response.put("totalCount", invoices.size());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/oracle/scm/inventory")
    public ResponseEntity<?> getOracleSCMInventory(@RequestParam(required = false) String location) {
        logger.info("Oracle SCM inventory API request for location: {}", location);
        
        Map<String, Object> response = new HashMap<>();
        response.put("systemId", "ORACLE_SCM_PROD");
        response.put("timestamp", LocalDateTime.now().format(FORMATTER));
        response.put("location", location != null ? location : "ALL_LOCATIONS");
        
        List<Map<String, Object>> inventory = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            inventory.add(Map.of(
                "itemNumber", "ITEM-" + String.format("%06d", i),
                "description", "Inventory Item " + i,
                "onHandQuantity", ThreadLocalRandom.current().nextInt(0, 5000),
                "availableQuantity", ThreadLocalRandom.current().nextInt(0, 4000),
                "reservedQuantity", ThreadLocalRandom.current().nextInt(0, 1000),
                "unitOfMeasure", getRandomStatus(Arrays.asList("EA", "KG", "LB", "M", "FT")),
                "unitCost", ThreadLocalRandom.current().nextDouble(1.0, 500.0),
                "lastMovementDate", LocalDateTime.now().minusDays(ThreadLocalRandom.current().nextInt(1, 30)).format(FORMATTER),
                "reorderLevel", ThreadLocalRandom.current().nextInt(100, 1000)
            ));
        }
        
        response.put("inventory", inventory);
        response.put("totalCount", inventory.size());
        
        return ResponseEntity.ok(response);
    }
    
    // Weather and External Data APIs
    
    @GetMapping("/weather/forecast")
    public ResponseEntity<?> getWeatherForecast(@RequestParam String location) {
        logger.info("Weather forecast API request for location: {}", location);
        
        Map<String, Object> response = new HashMap<>();
        response.put("location", location);
        response.put("timestamp", LocalDateTime.now().format(FORMATTER));
        response.put("provider", "WeatherPro Enterprise");
        
        List<Map<String, Object>> forecast = new ArrayList<>();
        for (int day = 0; day < 7; day++) {
            LocalDateTime forecastDate = LocalDateTime.now().plusDays(day);
            forecast.add(Map.of(
                "date", forecastDate.format(FORMATTER),
                "temperature", Map.of(
                    "high", ThreadLocalRandom.current().nextInt(15, 35),
                    "low", ThreadLocalRandom.current().nextInt(5, 20)
                ),
                "humidity", ThreadLocalRandom.current().nextInt(30, 90),
                "windSpeed", ThreadLocalRandom.current().nextInt(5, 25),
                "precipitation", ThreadLocalRandom.current().nextDouble(0, 10),
                "conditions", getRandomStatus(Arrays.asList("Clear", "Partly Cloudy", "Cloudy", "Rain", "Storm")),
                "riskLevel", getRandomStatus(Arrays.asList("LOW", "MEDIUM", "HIGH"))
            ));
        }
        
        response.put("forecast", forecast);
        response.put("alerts", generateWeatherAlerts(location));
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/news/supply-chain")
    public ResponseEntity<?> getSupplyChainNews(@RequestParam(required = false) String region) {
        logger.info("Supply chain news API request for region: {}", region);
        
        Map<String, Object> response = new HashMap<>();
        response.put("region", region != null ? region : "GLOBAL");
        response.put("timestamp", LocalDateTime.now().format(FORMATTER));
        response.put("provider", "Global Trade Intelligence");
        
        List<Map<String, Object>> articles = Arrays.asList(
            Map.of(
                "headline", "Port Strike Affects East Coast Shipping",
                "summary", "Labor disputes at major ports causing delays in container processing",
                "impact", "HIGH",
                "category", "LOGISTICS",
                "publishedAt", LocalDateTime.now().minusHours(2).format(FORMATTER),
                "source", "Trade Weekly",
                "relevanceScore", 0.95
            ),
            Map.of(
                "headline", "New Trade Regulations Announced",
                "summary", "Updated customs procedures may affect international shipments",
                "impact", "MEDIUM",
                "category", "REGULATORY",
                "publishedAt", LocalDateTime.now().minusHours(6).format(FORMATTER),
                "source", "Regulatory Monitor",
                "relevanceScore", 0.78
            ),
            Map.of(
                "headline", "Technology Company Expands Manufacturing",
                "summary", "Major supplier announces new facility opening",
                "impact", "LOW",
                "category", "BUSINESS",
                "publishedAt", LocalDateTime.now().minusHours(12).format(FORMATTER),
                "source", "Industry Insider",
                "relevanceScore", 0.62
            )
        );
        
        response.put("articles", articles);
        response.put("totalCount", articles.size());
        
        return ResponseEntity.ok(response);
    }
    
    // Financial and Currency APIs
    
    @GetMapping("/currency/rates")
    public ResponseEntity<?> getCurrencyRates(@RequestParam(required = false) String baseCurrency) {
        logger.info("Currency rates API request with base: {}", baseCurrency);
        
        Map<String, Object> response = new HashMap<>();
        response.put("baseCurrency", baseCurrency != null ? baseCurrency : "USD");
        response.put("timestamp", LocalDateTime.now().format(FORMATTER));
        response.put("provider", "Financial Exchange Pro");
        
        Map<String, Object> rates = Map.of(
            "EUR", 0.85 + ThreadLocalRandom.current().nextDouble(-0.05, 0.05),
            "GBP", 0.75 + ThreadLocalRandom.current().nextDouble(-0.03, 0.03),
            "JPY", 110.0 + ThreadLocalRandom.current().nextDouble(-5.0, 5.0),
            "CNY", 6.45 + ThreadLocalRandom.current().nextDouble(-0.2, 0.2),
            "CAD", 1.25 + ThreadLocalRandom.current().nextDouble(-0.05, 0.05)
        );
        
        response.put("rates", rates);
        response.put("lastUpdated", LocalDateTime.now().minusMinutes(ThreadLocalRandom.current().nextInt(1, 30)).format(FORMATTER));
        
        return ResponseEntity.ok(response);
    }
    
    // Government and Regulatory APIs
    
    @GetMapping("/customs/tariffs")
    public ResponseEntity<?> getCustomsTariffs(@RequestParam String country, @RequestParam String category) {
        logger.info("Customs tariffs API request for country: {}, category: {}", country, category);
        
        Map<String, Object> response = new HashMap<>();
        response.put("country", country);
        response.put("category", category);
        response.put("timestamp", LocalDateTime.now().format(FORMATTER));
        response.put("authority", "International Trade Commission");
        
        List<Map<String, Object>> tariffs = Arrays.asList(
            Map.of(
                "hsCode", "8471.30.01",
                "description", "Portable digital computers",
                "rate", ThreadLocalRandom.current().nextDouble(0, 15),
                "unit", "PERCENT",
                "effectiveDate", "2024-01-01T00:00:00Z",
                "status", "ACTIVE"
            ),
            Map.of(
                "hsCode", "8471.41.01", 
                "description", "Computer processing units",
                "rate", ThreadLocalRandom.current().nextDouble(0, 20),
                "unit", "PERCENT",
                "effectiveDate", "2024-01-01T00:00:00Z",
                "status", "ACTIVE"
            )
        );
        
        response.put("tariffs", tariffs);
        response.put("totalCount", tariffs.size());
        
        return ResponseEntity.ok(response);
    }
    
    // Crisis Simulation APIs
    
    @PostMapping("/crisis/simulate")
    public ResponseEntity<?> simulateCrisis(@RequestBody Map<String, Object> crisisConfig) {
        String crisisType = (String) crisisConfig.get("type");
        Integer severity = (Integer) crisisConfig.getOrDefault("severity", 3);
        
        logger.info("Crisis simulation API request - type: {}, severity: {}", crisisType, severity);
        
        Map<String, Object> response = new HashMap<>();
        response.put("crisisId", "CRISIS-" + System.currentTimeMillis());
        response.put("type", crisisType);
        response.put("severity", severity);
        response.put("startTime", LocalDateTime.now().format(FORMATTER));
        response.put("status", "ACTIVE");
        
        // Generate crisis-specific impacts
        Map<String, Object> impacts = generateCrisisImpacts(crisisType, severity);
        response.put("impacts", impacts);
        
        // Generate affected systems
        List<String> affectedSystems = generateAffectedSystems(crisisType, severity);
        response.put("affectedSystems", affectedSystems);
        
        return ResponseEntity.ok(response);
    }
    
    // Simulation Control APIs
    
    @PostMapping("/simulation/configure")
    public ResponseEntity<?> configureSimulation(@RequestBody Map<String, Object> config) {
        logger.info("Simulation configuration request: {}", config);
        
        Map<String, Object> response = new HashMap<>();
        response.put("simulationId", "SIM-" + System.currentTimeMillis());
        response.put("configuration", config);
        response.put("status", "CONFIGURED");
        response.put("timestamp", LocalDateTime.now().format(FORMATTER));
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/simulation/status")
    public ResponseEntity<?> getSimulationStatus() {
        logger.info("Simulation status request");
        
        Map<String, Object> response = new HashMap<>();
        response.put("activeSimulations", ThreadLocalRandom.current().nextInt(0, 5));
        response.put("systemLoad", ThreadLocalRandom.current().nextDouble(0.1, 0.8));
        response.put("mockAPIsEnabled", true);
        response.put("lastRestart", LocalDateTime.now().minusHours(ThreadLocalRandom.current().nextInt(1, 72)).format(FORMATTER));
        response.put("healthStatus", "HEALTHY");
        
        return ResponseEntity.ok(response);
    }
    
    // Helper methods
    
    private String getRandomStatus(List<String> options) {
        return options.get(ThreadLocalRandom.current().nextInt(options.size()));
    }
    
    private List<Map<String, Object>> generateWeatherAlerts(String location) {
        if (ThreadLocalRandom.current().nextDouble() < 0.3) { // 30% chance of alerts
            return Arrays.asList(
                Map.of(
                    "type", "SEVERE_WEATHER",
                    "severity", getRandomStatus(Arrays.asList("WATCH", "WARNING", "ADVISORY")),
                    "description", "Potential weather disruption in " + location,
                    "effectiveFrom", LocalDateTime.now().format(FORMATTER),
                    "effectiveUntil", LocalDateTime.now().plusHours(24).format(FORMATTER)
                )
            );
        }
        return new ArrayList<>();
    }
    
    private Map<String, Object> generateCrisisImpacts(String crisisType, Integer severity) {
        Map<String, Object> impacts = new HashMap<>();
        
        switch (crisisType.toUpperCase()) {
            case "HURRICANE":
                impacts.put("shippingDelays", severity * 2 + " to " + (severity * 4) + " days");
                impacts.put("portClosures", severity > 3 ? "Multiple ports affected" : "Limited port disruption");
                impacts.put("costIncrease", (severity * 10) + "%");
                break;
            case "CYBER_ATTACK":
                impacts.put("systemsOffline", (severity * 20) + "%");
                impacts.put("dataIntegrity", severity > 3 ? "COMPROMISED" : "INTACT");
                impacts.put("recoveryTime", (severity * 12) + " hours");
                break;
            case "TRADE_WAR":
                impacts.put("tariffIncrease", (severity * 5) + "%");
                impacts.put("routeChanges", "Alternative sourcing required");
                impacts.put("compliance", "New documentation required");
                break;
            default:
                impacts.put("operationalImpact", (severity * 15) + "%");
                impacts.put("duration", (severity * 2) + " weeks");
        }
        
        return impacts;
    }
    
    private List<String> generateAffectedSystems(String crisisType, Integer severity) {
        List<String> systems = new ArrayList<>();
        
        systems.add("Supplier Management");
        systems.add("Shipment Tracking");
        
        if (severity > 2) {
            systems.add("Financial Systems");
            systems.add("Inventory Management");
        }
        
        if (severity > 3) {
            systems.add("Production Planning");
            systems.add("Customer Communications");
        }
        
        return systems;
    }
}
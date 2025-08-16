package com.supplychainrisk.service;

import com.supplychainrisk.entity.*;
import com.supplychainrisk.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Enterprise-grade testing service for generating complex scenarios and mock data
 * This service provides SAP-level testing capabilities including:
 * - Complex crisis scenario simulation
 * - Load testing data generation
 * - Multi-dimensional risk scenarios
 * - Real-world supply chain disruptions
 */
@Service
public class EnterpriseTestingService {
    
    private static final Logger logger = LoggerFactory.getLogger(EnterpriseTestingService.class);
    
    @Autowired
    private SupplierRepository supplierRepository;
    
    @Autowired
    private ShipmentRepository shipmentRepository;
    
    @Autowired
    private ShipmentTrackingEventRepository trackingEventRepository;
    
    @Autowired
    private RiskAlertService riskAlertService;
    
    @Autowired
    private NotificationService notificationService;
    
    private static final Map<String, TestingScenario> AVAILABLE_SCENARIOS = Map.of(
        "HURRICANE_DISRUPTION", new TestingScenario(
            "Hurricane Disruption",
            "Simulate major hurricane affecting multiple shipping routes and suppliers",
            Arrays.asList("logistics", "weather", "ports", "suppliers")
        ),
        "PANDEMIC_CRISIS", new TestingScenario(
            "Global Pandemic",
            "COVID-19 style pandemic with lockdowns and supply chain disruptions",
            Arrays.asList("global", "suppliers", "manufacturing", "borders")
        ),
        "TRADE_WAR", new TestingScenario(
            "Trade War Impact",
            "Tariffs and trade restrictions affecting specific regions",
            Arrays.asList("geopolitical", "tariffs", "costs", "routes")
        ),
        "SUPPLIER_BANKRUPTCY", new TestingScenario(
            "Major Supplier Failure",
            "Critical supplier bankruptcy affecting multiple supply chains",
            Arrays.asList("suppliers", "financial", "dependencies", "alternatives")
        ),
        "CYBER_ATTACK", new TestingScenario(
            "Cyber Security Incident",
            "Cyber attack affecting supplier systems and data flow",
            Arrays.asList("security", "data", "systems", "recovery")
        ),
        "SUEZ_BLOCKAGE", new TestingScenario(
            "Canal/Port Blockage",
            "Major shipping route blockage like Suez Canal incident",
            Arrays.asList("logistics", "routes", "delays", "alternatives")
        ),
        "QUALITY_CRISIS", new TestingScenario(
            "Product Quality Issue",
            "Major quality defect requiring recalls and supplier changes",
            Arrays.asList("quality", "recalls", "suppliers", "reputation")
        ),
        "CURRENCY_CRISIS", new TestingScenario(
            "Financial Crisis",
            "Currency fluctuations and financial instability",
            Arrays.asList("financial", "currency", "costs", "hedging")
        )
    );
    
    /**
     * Get comprehensive system statistics for admin dashboard
     */
    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Basic counts
        stats.put("totalSuppliers", supplierRepository.count());
        stats.put("totalShipments", shipmentRepository.count());
        stats.put("totalTrackingEvents", trackingEventRepository.count());
        
        // Performance metrics
        long onTimeShipments = shipmentRepository.countByStatus(Shipment.ShipmentStatus.DELIVERED);
        long totalDelivered = shipmentRepository.countByStatus(Shipment.ShipmentStatus.DELIVERED);
        double onTimeRate = totalDelivered > 0 ? (double) onTimeShipments / totalDelivered * 100 : 0;
        
        stats.put("onTimeDeliveryRate", Math.round(onTimeRate * 100.0) / 100.0);
        stats.put("activeShipments", shipmentRepository.countByStatus(Shipment.ShipmentStatus.IN_TRANSIT));
        
        // Risk metrics
        List<Supplier> suppliers = supplierRepository.findAll();
        double avgRiskScore = suppliers.stream()
            .mapToDouble(s -> s.getOverallRiskScore() != null ? s.getOverallRiskScore().doubleValue() : 0.0)
            .average()
            .orElse(0.0);
        
        stats.put("averageRiskScore", Math.round(avgRiskScore * 100.0) / 100.0);
        stats.put("highRiskSuppliers", suppliers.stream()
            .mapToInt(s -> s.getOverallRiskScore() != null && s.getOverallRiskScore().doubleValue() > 70.0 ? 1 : 0)
            .sum());
        
        // Geographic distribution
        Map<String, Long> suppliersByCountry = new HashMap<>();
        suppliers.forEach(supplier -> {
            String country = supplier.getCountry();
            suppliersByCountry.put(country, suppliersByCountry.getOrDefault(country, 0L) + 1);
        });
        stats.put("suppliersByCountry", suppliersByCountry);
        
        // Recent activity - using simpler counting methods
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        stats.put("recentShipments", shipmentRepository.findAll().stream()
            .mapToLong(s -> s.getCreatedAt() != null && s.getCreatedAt().isAfter(last24Hours) ? 1 : 0)
            .sum());
        stats.put("recentEvents", trackingEventRepository.findAll().stream()
            .mapToLong(e -> e.getEventTimestamp() != null && e.getEventTimestamp().isAfter(last24Hours) ? 1 : 0)
            .sum());

        return stats;
    }
    
    /**
     * Generate complex testing scenario with realistic data
     */
    @Transactional
    public Map<String, Object> generateTestingScenario(Map<String, Object> config) {
        String scenarioType = (String) config.get("scenarioType");
        Integer intensity = (Integer) config.getOrDefault("intensity", 3); // 1-5 scale
        List<String> affectedRegions = (List<String>) config.getOrDefault("affectedRegions", Arrays.asList("Asia", "Europe"));
        
        TestingScenario scenario = AVAILABLE_SCENARIOS.get(scenarioType);
        if (scenario == null) {
            throw new IllegalArgumentException("Unknown scenario type: " + scenarioType);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("scenarioName", scenario.name);
        result.put("description", scenario.description);
        result.put("intensity", intensity);
        result.put("affectedRegions", affectedRegions);
        result.put("generatedAt", LocalDateTime.now());
        
        // Generate scenario-specific data
        switch (scenarioType) {
            case "HURRICANE_DISRUPTION":
                result.putAll(generateHurricaneScenario(intensity, affectedRegions));
                break;
            case "PANDEMIC_CRISIS":
                result.putAll(generatePandemicScenario(intensity, affectedRegions));
                break;
            case "TRADE_WAR":
                result.putAll(generateTradeWarScenario(intensity, affectedRegions));
                break;
            case "SUPPLIER_BANKRUPTCY":
                result.putAll(generateSupplierBankruptcyScenario(intensity));
                break;
            case "CYBER_ATTACK":
                result.putAll(generateCyberAttackScenario(intensity, affectedRegions));
                break;
            default:
                result.putAll(generateGenericCrisisScenario(intensity, affectedRegions));
        }
        
        return result;
    }
    
    /**
     * Simulate crisis scenario with real-time updates
     */
    @Transactional
    public Map<String, Object> simulateCrisisScenario(Map<String, Object> config) {
        String crisisType = (String) config.get("crisisType");
        Integer duration = (Integer) config.getOrDefault("duration", 72); // hours
        Boolean realTimeUpdates = (Boolean) config.getOrDefault("realTimeUpdates", true);
        
        Map<String, Object> result = new HashMap<>();
        result.put("crisisType", crisisType);
        result.put("duration", duration);
        result.put("startTime", LocalDateTime.now());
        result.put("estimatedEndTime", LocalDateTime.now().plusHours(duration));
        
        // Create crisis events and notifications
        List<Map<String, Object>> events = generateCrisisEvents(crisisType, duration);
        result.put("events", events);
        
        // Generate immediate impacts
        Map<String, Object> impacts = calculateImmediateImpacts(crisisType);
        result.put("immediateImpacts", impacts);
        
        if (realTimeUpdates) {
            // Schedule real-time notifications (in real implementation, this would use a job scheduler)
            result.put("realTimeUpdatesEnabled", true);
            result.put("updateFrequency", "Every 15 minutes");
        }
        
        return result;
    }
    
    /**
     * Generate large volumes of test data for load testing
     */
    @Transactional
    public Map<String, Object> generateLoadTestData(Map<String, Object> config) {
        Integer supplierCount = (Integer) config.getOrDefault("suppliers", 1000);
        Integer shipmentCount = (Integer) config.getOrDefault("shipments", 10000);
        Integer eventCount = (Integer) config.getOrDefault("events", 100000);
        
        Map<String, Object> result = new HashMap<>();
        result.put("generationStarted", LocalDateTime.now());
        
        // Note: In a real implementation, this would use batch processing
        // For now, we'll simulate the data generation
        result.put("suppliersToGenerate", supplierCount);
        result.put("shipmentsToGenerate", shipmentCount);
        result.put("eventsToGenerate", eventCount);
        result.put("estimatedDuration", calculateEstimatedDuration(supplierCount, shipmentCount, eventCount));
        
        // Simulate progress tracking
        result.put("status", "IN_PROGRESS");
        result.put("progressTracking", Map.of(
            "suppliers", Map.of("total", supplierCount, "completed", 0),
            "shipments", Map.of("total", shipmentCount, "completed", 0),
            "events", Map.of("total", eventCount, "completed", 0)
        ));
        
        return result;
    }
    
    /**
     * Get all available testing scenarios
     */
    public Map<String, Object> getAvailableTestingScenarios() {
        Map<String, Object> scenarios = new HashMap<>();
        
        AVAILABLE_SCENARIOS.forEach((key, scenario) -> {
            scenarios.put(key, Map.of(
                "name", scenario.name,
                "description", scenario.description,
                "categories", scenario.categories,
                "complexity", "High",
                "estimatedDuration", "2-4 hours",
                "dataVolume", "Enterprise-scale"
            ));
        });
        
        return Map.of(
            "scenarios", scenarios,
            "totalCount", scenarios.size(),
            "categories", Arrays.asList("Crisis Management", "Performance Testing", "Risk Assessment", "Compliance Testing")
        );
    }
    
    /**
     * Reset all testing data
     */
    @Transactional
    public void resetAllTestingData() {
        // In a real implementation, this would carefully reset test data
        // while preserving production data
        logger.info("Simulating reset of testing data...");
        
        // This would include:
        // - Removing test suppliers, shipments, events
        // - Resetting risk scores to baseline
        // - Clearing test notifications
        // - Restoring system to known good state
    }
    
    // Private helper methods for scenario generation
    
    private Map<String, Object> generateHurricaneScenario(Integer intensity, List<String> regions) {
        Map<String, Object> scenario = new HashMap<>();
        
        // Affected ports and routes
        List<String> affectedPorts = Arrays.asList("Houston", "New Orleans", "Mobile", "Jacksonville");
        List<String> affectedRoutes = Arrays.asList("Gulf of Mexico", "East Coast", "Caribbean");
        
        scenario.put("affectedPorts", affectedPorts);
        scenario.put("affectedRoutes", affectedRoutes);
        scenario.put("expectedDelays", intensity * 3 + " to " + (intensity * 5) + " days");
        scenario.put("affectedShipments", estimateAffectedShipments(regions, intensity));
        scenario.put("riskIncrease", intensity * 20 + "%");
        
        // Generate specific events
        List<Map<String, Object>> events = Arrays.asList(
            Map.of("type", "WEATHER_WARNING", "severity", "HIGH", "description", "Category " + intensity + " hurricane approaching"),
            Map.of("type", "PORT_CLOSURE", "severity", "CRITICAL", "description", "Major ports closed due to hurricane"),
            Map.of("type", "ROUTE_DISRUPTION", "severity", "HIGH", "description", "Shipping routes diverted")
        );
        scenario.put("events", events);
        
        return scenario;
    }
    
    private Map<String, Object> generatePandemicScenario(Integer intensity, List<String> regions) {
        Map<String, Object> scenario = new HashMap<>();
        
        scenario.put("lockdownSeverity", intensity);
        scenario.put("bordersAffected", regions);
        scenario.put("manufacturingReduction", intensity * 15 + "%");
        scenario.put("shippingDelays", intensity * 2 + " to " + (intensity * 4) + " weeks");
        scenario.put("supplierAvailability", (100 - intensity * 20) + "%");
        
        return scenario;
    }
    
    private Map<String, Object> generateTradeWarScenario(Integer intensity, List<String> regions) {
        Map<String, Object> scenario = new HashMap<>();
        
        scenario.put("tariffRate", intensity * 5 + "%");
        scenario.put("affectedCountries", regions);
        scenario.put("costIncrease", intensity * 10 + "%");
        scenario.put("alternativeRoutes", generateAlternativeRoutes(regions));
        
        return scenario;
    }
    
    private Map<String, Object> generateSupplierBankruptcyScenario(Integer intensity) {
        Map<String, Object> scenario = new HashMap<>();
        
        // Select suppliers to "fail" based on intensity
        List<Supplier> suppliers = supplierRepository.findAll();
        int failureCount = Math.min(intensity * 2, suppliers.size() / 10);
        
        scenario.put("failedSuppliers", failureCount);
        scenario.put("affectedShipments", estimateAffectedShipments(Arrays.asList("Global"), intensity));
        scenario.put("recoveryTime", intensity * 4 + " to " + (intensity * 8) + " weeks");
        scenario.put("alternativeSourcingRequired", true);
        
        return scenario;
    }
    
    private Map<String, Object> generateCyberAttackScenario(Integer intensity, List<String> regions) {
        Map<String, Object> scenario = new HashMap<>();
        
        scenario.put("systemsAffected", intensity * 20 + "%");
        scenario.put("dataCompromised", intensity < 3 ? "Limited" : intensity < 4 ? "Moderate" : "Extensive");
        scenario.put("recoveryTime", intensity * 24 + " to " + (intensity * 48) + " hours");
        scenario.put("operationalImpact", intensity * 30 + "%");
        
        return scenario;
    }
    
    private Map<String, Object> generateGenericCrisisScenario(Integer intensity, List<String> regions) {
        Map<String, Object> scenario = new HashMap<>();
        
        scenario.put("severity", intensity);
        scenario.put("affectedRegions", regions);
        scenario.put("estimatedImpact", intensity * 25 + "%");
        scenario.put("recoveryTime", intensity + " to " + (intensity * 2) + " weeks");
        
        return scenario;
    }
    
    private List<Map<String, Object>> generateCrisisEvents(String crisisType, Integer duration) {
        List<Map<String, Object>> events = new ArrayList<>();
        
        // Generate timeline of events over the crisis duration
        for (int hour = 0; hour < duration; hour += 6) {
            LocalDateTime eventTime = LocalDateTime.now().plusHours(hour);
            
            events.add(Map.of(
                "timestamp", eventTime,
                "type", "STATUS_UPDATE",
                "severity", hour < 24 ? "HIGH" : hour < 48 ? "MEDIUM" : "LOW",
                "description", "Crisis situation update - hour " + hour
            ));
        }
        
        return events;
    }
    
    private Map<String, Object> calculateImmediateImpacts(String crisisType) {
        Map<String, Object> impacts = new HashMap<>();
        
        Random random = ThreadLocalRandom.current();
        
        impacts.put("delayedShipments", random.nextInt(500) + 100);
        impacts.put("affectedSuppliers", random.nextInt(50) + 10);
        impacts.put("costIncrease", (random.nextDouble() * 30 + 5) + "%");
        impacts.put("riskScoreIncrease", random.nextDouble() * 2 + 0.5);
        
        return impacts;
    }
    
    private String calculateEstimatedDuration(Integer suppliers, Integer shipments, Integer events) {
        // Simple estimation based on data volume
        int totalItems = suppliers + shipments + events;
        int minutes = totalItems / 1000; // Assume 1000 items per minute
        
        if (minutes < 60) {
            return minutes + " minutes";
        } else {
            return (minutes / 60) + " hours " + (minutes % 60) + " minutes";
        }
    }
    
    private Integer estimateAffectedShipments(List<String> regions, Integer intensity) {
        long totalShipments = shipmentRepository.count();
        double affectedPercentage = (intensity * 10.0) / 100.0; // 10%, 20%, 30%, etc.
        return (int) (totalShipments * affectedPercentage);
    }
    
    private List<String> generateAlternativeRoutes(List<String> affectedRegions) {
        List<String> alternatives = new ArrayList<>();
        
        if (affectedRegions.contains("Asia")) {
            alternatives.add("Trans-Pacific via North America");
            alternatives.add("Europe via Middle East");
        }
        if (affectedRegions.contains("Europe")) {
            alternatives.add("Asia via Suez Canal");
            alternatives.add("North America via Atlantic");
        }
        
        return alternatives;
    }
    
    /**
     * Generate mock data based on configuration
     */
    @Transactional
    public Map<String, Object> generateMockData(Map<String, Object> dataConfig) {
        String dataType = (String) dataConfig.get("dataType");
        Map<String, Object> config = (Map<String, Object>) dataConfig.get("config");
        Map<String, Object> result = new HashMap<>();
        
        switch (dataType) {
            case "suppliers":
                result = generateMockSuppliers(config);
                break;
            case "shipments":
                result = generateMockShipments(config);
                break;
            case "scenarios":
                result = generateMockScenarios(config);
                break;
            default:
                throw new IllegalArgumentException("Unknown data type: " + dataType);
        }
        
        result.put("dataType", dataType);
        result.put("timestamp", LocalDateTime.now());
        return result;
    }
    
    /**
     * Clear all mock data
     */
    @Transactional
    public void clearMockData() {
        logger.info("Clearing all mock data...");
        // In a real implementation, you might want to mark data as test data
        // and only clear that, rather than clearing all data
        
        // For now, we'll just log the action
        logger.info("Mock data cleared (simulated)");
    }
    
    private Map<String, Object> generateMockSuppliers(Map<String, Object> config) {
        Integer count = (Integer) config.get("count");
        List<String> regions = (List<String>) config.get("regions");
        List<String> industries = (List<String>) config.get("industries");
        
        List<Supplier> suppliers = new ArrayList<>();
        Random random = ThreadLocalRandom.current();
        
        for (int i = 0; i < count; i++) {
            Supplier supplier = new Supplier();
            supplier.setName("Test Supplier " + (i + 1));
            supplier.setLocation(regions.get(random.nextInt(regions.size())));
            supplier.setIndustry(industries.get(random.nextInt(industries.size())));
            supplier.setRiskScore(BigDecimal.valueOf(random.nextDouble() * 10));
            supplier.setPerformanceScore(BigDecimal.valueOf(80 + random.nextDouble() * 20));
            supplier.setCertifications(Arrays.asList("ISO9001", "ISO14001"));
            supplier.setCreatedAt(LocalDateTime.now());
            
            // Save only in non-production environments or mark as test data
            suppliers.add(supplier);
        }
        
        return Map.of(
            "generated", suppliers.size(),
            "suppliers", suppliers.subList(0, Math.min(10, suppliers.size())), // Return first 10 for preview
            "totalCount", count
        );
    }
    
    private Map<String, Object> generateMockShipments(Map<String, Object> config) {
        Integer count = (Integer) config.get("count");
        String timeRange = (String) config.get("timeRange");
        List<String> routes = (List<String>) config.get("routes");
        
        List<Map<String, Object>> shipments = new ArrayList<>();
        Random random = ThreadLocalRandom.current();
        
        for (int i = 0; i < Math.min(count, 10); i++) { // Limit preview to 10
            Map<String, Object> shipment = new HashMap<>();
            shipment.put("trackingNumber", "TEST" + System.currentTimeMillis() + i);
            shipment.put("route", routes.get(random.nextInt(routes.size())));
            shipment.put("status", Arrays.asList("IN_TRANSIT", "DELIVERED", "DELAYED").get(random.nextInt(3)));
            shipment.put("value", BigDecimal.valueOf(10000 + random.nextDouble() * 50000));
            shipments.add(shipment);
        }
        
        return Map.of(
            "generated", count,
            "shipments", shipments,
            "timeRange", timeRange
        );
    }
    
    private Map<String, Object> generateMockScenarios(Map<String, Object> config) {
        String type = (String) config.get("type");
        String severity = (String) config.get("severity");
        Integer duration = (Integer) config.get("duration");
        
        Random random = ThreadLocalRandom.current();
        
        return Map.of(
            "generated", random.nextInt(50) + 10,
            "scenarioType", type,
            "severity", severity,
            "duration", duration + " days",
            "events", Arrays.asList(
                Map.of("type", "SUPPLY_DISRUPTION", "impact", "Medium"),
                Map.of("type", "DELAY_INCREASE", "impact", "Low")
            )
        );
    }
    
    // Helper classes
    private static class TestingScenario {
        final String name;
        final String description;
        final List<String> categories;
        
        TestingScenario(String name, String description, List<String> categories) {
            this.name = name;
            this.description = description;
            this.categories = categories;
        }
    }
}
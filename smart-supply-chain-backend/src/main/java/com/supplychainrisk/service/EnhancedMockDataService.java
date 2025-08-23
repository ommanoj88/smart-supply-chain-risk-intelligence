package com.supplychainrisk.service;

import com.supplychainrisk.dto.CrisisScenarioRequest;
import com.supplychainrisk.dto.MarketDataRequest;
import com.supplychainrisk.dto.SupplierScenarioRequest;
import com.supplychainrisk.entity.Supplier;
import com.supplychainrisk.entity.Shipment;
import com.supplychainrisk.repository.SupplierRepository;
import com.supplychainrisk.repository.ShipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Enhanced Mock Data Service for comprehensive test data generation
 * Supporting crisis scenarios, market data simulation, and advanced supplier scenarios
 */
@Service
public class EnhancedMockDataService {
    
    private static final Logger logger = LoggerFactory.getLogger(EnhancedMockDataService.class);
    
    @Autowired
    private SupplierRepository supplierRepository;
    
    @Autowired
    private ShipmentRepository shipmentRepository;
    
    private final Random random = ThreadLocalRandom.current();
    
    // Hurricane-prone regions and major ports
    private static final Map<String, List<String>> HURRICANE_REGIONS = Map.of(
        "Gulf Coast", Arrays.asList("Houston", "New Orleans", "Mobile", "Tampa"),
        "East Coast", Arrays.asList("Jacksonville", "Savannah", "Charleston", "Norfolk"),
        "Caribbean", Arrays.asList("Puerto Rico", "Dominican Republic", "Jamaica", "Bahamas")
    );
    
    // Major trade routes and alternate routes
    private static final Map<String, List<String>> TRADE_ROUTES = Map.of(
        "Asia-US", Arrays.asList("Trans-Pacific", "Panama Canal", "Suez-Atlantic"),
        "Europe-US", Arrays.asList("North Atlantic", "Mediterranean-Atlantic"),
        "Asia-Europe", Arrays.asList("Suez Canal", "Trans-Siberian", "Northern Sea Route")
    );
    
    // Currency pairs and typical volatility ranges
    private static final Map<String, Double> CURRENCY_VOLATILITY = Map.of(
        "USD-EUR", 0.12, "USD-JPY", 0.08, "USD-GBP", 0.15,
        "USD-CNY", 0.06, "USD-INR", 0.09, "EUR-GBP", 0.11
    );
    
    /**
     * Generate comprehensive crisis scenario with cascading effects
     */
    public Map<String, Object> generateCrisisScenario(CrisisScenarioRequest request) {
        String scenarioType = request.getScenarioType();
        
        logger.info("Generating enhanced crisis scenario: {}", scenarioType);
        
        switch (scenarioType) {
            case "HURRICANE":
                return generateHurricaneScenario(request);
            case "TRADE_WAR":
                return generateTradeWarScenario(request);
            case "SUPPLIER_BANKRUPTCY":
                return generateSupplierBankruptcyScenario(request);
            case "PANDEMIC":
                return generatePandemicScenario(request);
            default:
                throw new IllegalArgumentException("Unknown scenario type: " + scenarioType);
        }
    }
    
    /**
     * Generate realistic hurricane impact scenario
     */
    private Map<String, Object> generateHurricaneScenario(CrisisScenarioRequest request) {
        List<String> affectedRegions = request.getAffectedRegions();
        String severity = request.getSeverity(); // CATEGORY_1 to CATEGORY_5
        Duration impactDuration = request.getDuration();
        
        Map<String, Object> scenario = new HashMap<>();
        scenario.put("scenarioId", UUID.randomUUID().toString());
        scenario.put("scenarioType", "HURRICANE");
        scenario.put("severity", severity);
        scenario.put("generatedAt", LocalDateTime.now());
        
        // Determine impact level based on category
        int categoryLevel = Integer.parseInt(severity.replace("CATEGORY_", ""));
        double impactMultiplier = categoryLevel / 5.0; // Scale from 0.2 to 1.0
        
        // Find affected suppliers in hurricane-prone regions
        List<Supplier> affectedSuppliers = supplierRepository.findAll().stream()
            .filter(supplier -> isSupplierInHurricaneRegion(supplier, affectedRegions))
            .collect(Collectors.toList());
        
        // Generate realistic supplier impacts
        List<Map<String, Object>> supplierImpacts = new ArrayList<>();
        for (Supplier supplier : affectedSuppliers) {
            Map<String, Object> impact = simulateHurricaneSupplierImpact(supplier, categoryLevel, impactDuration);
            supplierImpacts.add(impact);
        }
        scenario.put("affectedSuppliers", supplierImpacts);
        
        // Generate shipping disruptions
        List<Map<String, Object>> shippingDisruptions = generateHurricaneShippingDisruptions(
            affectedSuppliers, categoryLevel, impactDuration);
        scenario.put("shippingDisruptions", shippingDisruptions);
        
        // Generate recovery timeline
        List<Map<String, Object>> recoveryPlan = generateHurricaneRecoveryPlan(
            affectedSuppliers, categoryLevel, impactDuration);
        scenario.put("recoveryPlan", recoveryPlan);
        
        // Calculate economic impact
        Map<String, Object> economicImpact = calculateHurricaneEconomicImpact(
            affectedSuppliers, shippingDisruptions, categoryLevel);
        scenario.put("economicImpact", economicImpact);
        
        // Generate affected ports and routes
        List<String> affectedPorts = getAffectedPortsForRegions(affectedRegions);
        scenario.put("affectedPorts", affectedPorts);
        
        // Expected delays and risk increases
        scenario.put("expectedDelays", (categoryLevel * 3) + " to " + (categoryLevel * 5) + " days");
        scenario.put("riskIncrease", (categoryLevel * 20) + "%");
        
        return scenario;
    }
    
    /**
     * Generate trade war scenario with tariff impacts
     */
    private Map<String, Object> generateTradeWarScenario(CrisisScenarioRequest request) {
        List<String> affectedRegions = request.getAffectedRegions();
        String severity = request.getSeverity(); // LOW, MEDIUM, HIGH
        
        Map<String, Object> scenario = new HashMap<>();
        scenario.put("scenarioId", UUID.randomUUID().toString());
        scenario.put("scenarioType", "TRADE_WAR");
        scenario.put("severity", severity);
        scenario.put("generatedAt", LocalDateTime.now());
        
        // Determine impact based on severity
        double severityMultiplier = switch (severity) {
            case "LOW" -> 0.3;
            case "MEDIUM" -> 0.6;
            case "HIGH" -> 1.0;
            default -> 0.5;
        };
        
        // Generate tariff impacts
        Map<String, Object> tariffImpacts = generateTariffImpacts(affectedRegions, severityMultiplier);
        scenario.put("tariffImpacts", tariffImpacts);
        
        // Generate border delays
        Map<String, Object> borderDelays = generateBorderDelays(affectedRegions, severityMultiplier);
        scenario.put("borderDelays", borderDelays);
        
        // Generate alternative routing scenarios
        List<Map<String, Object>> alternativeRoutes = generateAlternativeRoutingScenarios(affectedRegions);
        scenario.put("alternativeRoutes", alternativeRoutes);
        
        // Generate supply chain reshuffling scenarios
        Map<String, Object> supplyChainReshuffling = generateSupplyChainReshuffling(affectedRegions, severityMultiplier);
        scenario.put("supplyChainReshuffling", supplyChainReshuffling);
        
        return scenario;
    }
    
    /**
     * Generate supplier bankruptcy scenario with cascading effects
     */
    private Map<String, Object> generateSupplierBankruptcyScenario(CrisisScenarioRequest request) {
        String severity = request.getSeverity();
        
        Map<String, Object> scenario = new HashMap<>();
        scenario.put("scenarioId", UUID.randomUUID().toString());
        scenario.put("scenarioType", "SUPPLIER_BANKRUPTCY");
        scenario.put("severity", severity);
        scenario.put("generatedAt", LocalDateTime.now());
        
        // Select suppliers to fail based on severity
        int numFailures = switch (severity) {
            case "LOW" -> 1;
            case "MEDIUM" -> 3;
            case "HIGH" -> 5;
            default -> 2;
        };
        
        List<Supplier> allSuppliers = supplierRepository.findAll();
        List<Supplier> failedSuppliers = selectSuppliersForBankruptcy(allSuppliers, numFailures);
        
        // Generate cascading effects
        Map<String, Object> cascadingEffects = generateCascadingEffects(failedSuppliers);
        scenario.put("cascadingEffects", cascadingEffects);
        
        // Generate alternative sourcing requirements
        Map<String, Object> alternativeSourcing = generateAlternativeSourcing(failedSuppliers);
        scenario.put("alternativeSourcing", alternativeSourcing);
        
        // Calculate financial impact
        Map<String, Object> financialImpact = calculateBankruptcyFinancialImpact(failedSuppliers);
        scenario.put("financialImpact", financialImpact);
        
        // Generate recovery actions
        List<Map<String, Object>> recoveryActions = generateBankruptcyRecoveryActions(failedSuppliers);
        scenario.put("recoveryActions", recoveryActions);
        
        return scenario;
    }
    
    /**
     * Generate pandemic disruption scenario
     */
    private Map<String, Object> generatePandemicScenario(CrisisScenarioRequest request) {
        List<String> affectedRegions = request.getAffectedRegions();
        String severity = request.getSeverity();
        Duration duration = request.getDuration();
        
        Map<String, Object> scenario = new HashMap<>();
        scenario.put("scenarioId", UUID.randomUUID().toString());
        scenario.put("scenarioType", "PANDEMIC");
        scenario.put("severity", severity);
        scenario.put("duration", duration.toDays() + " days");
        scenario.put("generatedAt", LocalDateTime.now());
        
        // Generate factory shutdowns
        Map<String, Object> factoryShutdowns = generateFactoryShutdowns(affectedRegions, severity, duration);
        scenario.put("factoryShutdowns", factoryShutdowns);
        
        // Generate labor shortages
        Map<String, Object> laborShortages = generateLaborShortages(affectedRegions, severity);
        scenario.put("laborShortages", laborShortages);
        
        // Generate demand spikes/drops
        Map<String, Object> demandVariations = generateDemandVariations(severity);
        scenario.put("demandVariations", demandVariations);
        
        // Identify supply chain fragility points
        List<Map<String, Object>> fragilityPoints = identifySupplyChainFragilityPoints();
        scenario.put("fragilityPoints", fragilityPoints);
        
        return scenario;
    }
    
    /**
     * Generate market data scenarios
     */
    public Map<String, Object> generateMarketDataScenario(MarketDataRequest request) {
        String dataType = request.getDataType();
        
        logger.info("Generating market data scenario: {}", dataType);
        
        switch (dataType) {
            case "CURRENCY":
                return generateCurrencyFluctuationData(request);
            case "COMMODITY":
                return generateCommodityPriceData(request);
            case "WEATHER":
                return generateWeatherImpactData(request);
            case "ECONOMIC":
                return generateEconomicIndicatorData(request);
            default:
                throw new IllegalArgumentException("Unknown market data type: " + dataType);
        }
    }
    
    /**
     * Generate currency fluctuation data
     */
    private Map<String, Object> generateCurrencyFluctuationData(MarketDataRequest request) {
        String region = request.getRegion();
        Duration timeRange = request.getTimeRange();
        Double volatilityLevel = request.getVolatilityLevel();
        
        Map<String, Object> currencyData = new HashMap<>();
        currencyData.put("dataType", "CURRENCY");
        currencyData.put("region", region);
        currencyData.put("timeRange", timeRange.toDays() + " days");
        currencyData.put("generatedAt", LocalDateTime.now());
        
        // Generate real-time currency rate changes
        Map<String, List<Map<String, Object>>> rateChanges = generateCurrencyRateChanges(
            region, timeRange, volatilityLevel);
        currencyData.put("rateChanges", rateChanges);
        
        // Calculate cost impact on shipments
        Map<String, Object> costImpacts = calculateCurrencyImpactOnCosts(rateChanges);
        currencyData.put("costImpacts", costImpacts);
        
        // Generate hedging scenarios
        List<Map<String, Object>> hedgingScenarios = generateCurrencyHedgingScenarios(rateChanges);
        currencyData.put("hedgingScenarios", hedgingScenarios);
        
        return currencyData;
    }
    
    /**
     * Generate supplier performance scenarios
     */
    public Map<String, Object> generateSupplierPerformanceScenario(SupplierScenarioRequest request) {
        Long supplierId = request.getSupplierId();
        String scenarioType = request.getScenarioType();
        
        logger.info("Generating supplier performance scenario: {} for supplier {}", scenarioType, supplierId);
        
        Supplier supplier = supplierRepository.findById(supplierId)
            .orElseThrow(() -> new RuntimeException("Supplier not found: " + supplierId));
        
        switch (scenarioType) {
            case "PERFORMANCE_DEGRADATION":
                return generatePerformanceDegradationScenario(supplier, request);
            case "CAPACITY_CONSTRAINT":
                return generateCapacityConstraintScenario(supplier, request);
            case "QUALITY_ISSUE":
                return generateQualityIssueScenario(supplier, request);
            case "COMPLIANCE_ISSUE":
                return generateComplianceIssueScenario(supplier, request);
            default:
                throw new IllegalArgumentException("Unknown supplier scenario type: " + scenarioType);
        }
    }
    
    // Helper methods for hurricane scenario
    private boolean isSupplierInHurricaneRegion(Supplier supplier, List<String> affectedRegions) {
        // Check if supplier location matches hurricane-prone regions
        for (String region : affectedRegions) {
            if (HURRICANE_REGIONS.containsKey(region)) {
                List<String> regionCities = HURRICANE_REGIONS.get(region);
                if (regionCities.contains(supplier.getCity()) || 
                    supplier.getCountry().equals("United States") && 
                    (supplier.getStateProvince().equals("FL") || supplier.getStateProvince().equals("TX") ||
                     supplier.getStateProvince().equals("LA") || supplier.getStateProvince().equals("AL"))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private Map<String, Object> simulateHurricaneSupplierImpact(Supplier supplier, int categoryLevel, Duration duration) {
        Map<String, Object> impact = new HashMap<>();
        impact.put("supplierId", supplier.getId());
        impact.put("supplierName", supplier.getName());
        impact.put("location", supplier.getCity() + ", " + supplier.getStateProvince());
        
        // Calculate impact severity based on hurricane category
        double impactSeverity = categoryLevel / 5.0;
        
        // Operational impact
        int operationalReduction = (int) (impactSeverity * 100);
        impact.put("operationalCapacityReduction", operationalReduction + "%");
        
        // Expected downtime
        long downtimeDays = (long) (categoryLevel * 2 + random.nextGaussian() * 2);
        downtimeDays = Math.max(1, downtimeDays); // Minimum 1 day
        impact.put("expectedDowntimeDays", downtimeDays);
        
        // Infrastructure damage
        String damageLevel = switch (categoryLevel) {
            case 1, 2 -> "MINIMAL";
            case 3 -> "MODERATE";
            case 4 -> "SEVERE";
            case 5 -> "CATASTROPHIC";
            default -> "UNKNOWN";
        };
        impact.put("infrastructureDamage", damageLevel);
        
        // Recovery probability
        double recoveryProbability = Math.max(0.1, 1.0 - (impactSeverity * 0.3));
        impact.put("recoveryProbability", Math.round(recoveryProbability * 100) + "%");
        
        return impact;
    }
    
    private List<Map<String, Object>> generateHurricaneShippingDisruptions(
            List<Supplier> affectedSuppliers, int categoryLevel, Duration duration) {
        List<Map<String, Object>> disruptions = new ArrayList<>();
        
        // Find shipments from affected suppliers
        for (Supplier supplier : affectedSuppliers) {
            List<Shipment> supplierShipments = shipmentRepository.findBySupplier(supplier);
            
            for (Shipment shipment : supplierShipments) {
                if (random.nextDouble() < (categoryLevel / 5.0)) { // Probability based on category
                    Map<String, Object> disruption = new HashMap<>();
                    disruption.put("shipmentId", shipment.getId());
                    disruption.put("trackingNumber", shipment.getTrackingNumber());
                    disruption.put("originalRoute", shipment.getOriginCity() + " -> " + shipment.getDestinationCity());
                    
                    // Generate delay
                    int delayDays = categoryLevel * 2 + random.nextInt(categoryLevel * 2);
                    disruption.put("expectedDelayDays", delayDays);
                    
                    // Alternative routing
                    disruption.put("alternativeRouting", generateAlternativeRoute(shipment));
                    
                    // Additional costs
                    double additionalCost = shipment.getTotalCost().doubleValue() * (categoryLevel * 0.1);
                    disruption.put("additionalCost", BigDecimal.valueOf(additionalCost));
                    
                    disruptions.add(disruption);
                }
            }
        }
        
        return disruptions.size() > 50 ? disruptions.subList(0, 50) : disruptions; // Limit for demo
    }
    
    private List<Map<String, Object>> generateHurricaneRecoveryPlan(
            List<Supplier> affectedSuppliers, int categoryLevel, Duration duration) {
        List<Map<String, Object>> recoveryPlan = new ArrayList<>();
        
        // Phase 1: Immediate assessment (0-72 hours)
        Map<String, Object> phase1 = new HashMap<>();
        phase1.put("phase", "IMMEDIATE_ASSESSMENT");
        phase1.put("timeframe", "0-72 hours");
        phase1.put("activities", Arrays.asList(
            "Assess supplier facility damage",
            "Establish communication with affected suppliers",
            "Evaluate inventory levels",
            "Activate emergency response protocols"
        ));
        recoveryPlan.add(phase1);
        
        // Phase 2: Emergency sourcing (72 hours - 2 weeks)
        Map<String, Object> phase2 = new HashMap<>();
        phase2.put("phase", "EMERGENCY_SOURCING");
        phase2.put("timeframe", "72 hours - 2 weeks");
        phase2.put("activities", Arrays.asList(
            "Activate backup suppliers",
            "Implement emergency logistics routing",
            "Expedite critical shipments",
            "Coordinate with insurance providers"
        ));
        recoveryPlan.add(phase2);
        
        // Phase 3: Recovery and normalization (2-8 weeks)
        Map<String, Object> phase3 = new HashMap<>();
        phase3.put("phase", "RECOVERY_NORMALIZATION");
        int recoveryWeeks = categoryLevel * 2 + random.nextInt(4);
        phase3.put("timeframe", "2-" + (2 + recoveryWeeks) + " weeks");
        phase3.put("activities", Arrays.asList(
            "Support supplier facility repairs",
            "Gradually restore normal operations",
            "Optimize supply chain based on lessons learned",
            "Update risk assessment and contingency plans"
        ));
        recoveryPlan.add(phase3);
        
        return recoveryPlan;
    }
    
    private Map<String, Object> calculateHurricaneEconomicImpact(
            List<Supplier> affectedSuppliers, List<Map<String, Object>> shippingDisruptions, int categoryLevel) {
        Map<String, Object> economicImpact = new HashMap<>();
        
        // Calculate direct costs
        double directCosts = affectedSuppliers.size() * categoryLevel * 50000; // $50k per supplier per category level
        economicImpact.put("directCosts", BigDecimal.valueOf(directCosts));
        
        // Calculate shipping cost increases
        double shippingCostIncrease = shippingDisruptions.stream()
            .mapToDouble(d -> ((BigDecimal) d.get("additionalCost")).doubleValue())
            .sum();
        economicImpact.put("shippingCostIncrease", BigDecimal.valueOf(shippingCostIncrease));
        
        // Calculate insurance claims
        double insuranceClaims = directCosts * 0.7; // 70% covered by insurance
        economicImpact.put("expectedInsuranceClaims", BigDecimal.valueOf(insuranceClaims));
        
        // Calculate productivity loss
        double productivityLoss = affectedSuppliers.size() * categoryLevel * 25000; // $25k per supplier per category
        economicImpact.put("productivityLoss", BigDecimal.valueOf(productivityLoss));
        
        // Total economic impact
        double totalImpact = directCosts + shippingCostIncrease + productivityLoss;
        economicImpact.put("totalEconomicImpact", BigDecimal.valueOf(totalImpact));
        
        return economicImpact;
    }
    
    // Additional helper methods will be implemented in the next part...
    
    private List<String> getAffectedPortsForRegions(List<String> regions) {
        List<String> affectedPorts = new ArrayList<>();
        for (String region : regions) {
            if (HURRICANE_REGIONS.containsKey(region)) {
                affectedPorts.addAll(HURRICANE_REGIONS.get(region));
            }
        }
        return affectedPorts.stream().distinct().collect(Collectors.toList());
    }
    
    private String generateAlternativeRoute(Shipment shipment) {
        // Simplified alternative route generation
        String[] alternatives = {
            "Via Panama Canal (additional 3-5 days)",
            "Via Suez Canal (additional 7-10 days)",
            "Via rail through Mexico (additional 2-3 days)",
            "Via air freight (expedited, higher cost)"
        };
        return alternatives[random.nextInt(alternatives.length)];
    }
    
    // Placeholder implementations for other scenario methods
    private Map<String, Object> generateTariffImpacts(List<String> affectedRegions, double severityMultiplier) {
        Map<String, Object> tariffImpacts = new HashMap<>();
        tariffImpacts.put("averageTariffIncrease", (severityMultiplier * 25) + "%");
        tariffImpacts.put("affectedTradeVolume", (severityMultiplier * 1000000) + " USD");
        return tariffImpacts;
    }
    
    private Map<String, Object> generateBorderDelays(List<String> affectedRegions, double severityMultiplier) {
        Map<String, Object> borderDelays = new HashMap<>();
        borderDelays.put("averageDelayHours", (int) (severityMultiplier * 48));
        borderDelays.put("additionalInspections", (severityMultiplier * 100) + "%");
        return borderDelays;
    }
    
    private List<Map<String, Object>> generateAlternativeRoutingScenarios(List<String> affectedRegions) {
        // Simplified implementation
        return Arrays.asList(
            Map.of("route", "Alternative Port Route", "additionalCost", "15%", "additionalTime", "3-5 days"),
            Map.of("route", "Rail Alternative", "additionalCost", "8%", "additionalTime", "1-2 days")
        );
    }
    
    private Map<String, Object> generateSupplyChainReshuffling(List<String> affectedRegions, double severityMultiplier) {
        Map<String, Object> reshuffling = new HashMap<>();
        reshuffling.put("suppliersRequiringRelocation", (int) (severityMultiplier * 10));
        reshuffling.put("estimatedReshufflingCost", (severityMultiplier * 500000) + " USD");
        return reshuffling;
    }
    
    // More method implementations will follow...
    private List<Supplier> selectSuppliersForBankruptcy(List<Supplier> allSuppliers, int numFailures) {
        // Select suppliers with higher risk scores for bankruptcy simulation
        return allSuppliers.stream()
            .filter(s -> s.getOverallRiskScore() > 60) // High risk suppliers
            .limit(numFailures)
            .collect(Collectors.toList());
    }
    
    private Map<String, Object> generateCascadingEffects(List<Supplier> failedSuppliers) {
        Map<String, Object> effects = new HashMap<>();
        effects.put("dependentSuppliersAffected", failedSuppliers.size() * 3);
        effects.put("shipmentsDisrupted", failedSuppliers.size() * 25);
        effects.put("alternativeSourcingRequired", true);
        return effects;
    }
    
    private Map<String, Object> generateAlternativeSourcing(List<Supplier> failedSuppliers) {
        Map<String, Object> sourcing = new HashMap<>();
        sourcing.put("emergencyQualificationTime", "2-4 weeks");
        sourcing.put("costPremium", "25-40%");
        sourcing.put("qualityRisk", "ELEVATED");
        return sourcing;
    }
    
    private Map<String, Object> calculateBankruptcyFinancialImpact(List<Supplier> failedSuppliers) {
        Map<String, Object> impact = new HashMap<>();
        double totalImpact = failedSuppliers.size() * 150000; // $150k per failed supplier
        impact.put("totalFinancialImpact", BigDecimal.valueOf(totalImpact));
        impact.put("expeditedShippingCosts", BigDecimal.valueOf(totalImpact * 0.3));
        impact.put("qualityRiskCosts", BigDecimal.valueOf(totalImpact * 0.2));
        return impact;
    }
    
    private List<Map<String, Object>> generateBankruptcyRecoveryActions(List<Supplier> failedSuppliers) {
        return Arrays.asList(
            Map.of("action", "Emergency Supplier Qualification", "timeframe", "2-4 weeks", "priority", "HIGH"),
            Map.of("action", "Supply Chain Risk Assessment", "timeframe", "1-2 weeks", "priority", "HIGH"),
            Map.of("action", "Contingency Plan Update", "timeframe", "4-6 weeks", "priority", "MEDIUM")
        );
    }
    
    // Pandemic scenario helper methods
    private Map<String, Object> generateFactoryShutdowns(List<String> affectedRegions, String severity, Duration duration) {
        Map<String, Object> shutdowns = new HashMap<>();
        double shutdownPercentage = switch (severity) {
            case "LOW" -> 0.2;
            case "MEDIUM" -> 0.5;
            case "HIGH" -> 0.8;
            default -> 0.4;
        };
        shutdowns.put("factoriesShutDown", (shutdownPercentage * 100) + "%");
        shutdowns.put("shutdownDuration", duration.toDays() + " days");
        return shutdowns;
    }
    
    private Map<String, Object> generateLaborShortages(List<String> affectedRegions, String severity) {
        Map<String, Object> shortages = new HashMap<>();
        double shortagePercentage = switch (severity) {
            case "LOW" -> 0.15;
            case "MEDIUM" -> 0.35;
            case "HIGH" -> 0.60;
            default -> 0.25;
        };
        shortages.put("workforceReduction", (shortagePercentage * 100) + "%");
        shortages.put("productivityImpact", ((shortagePercentage * 1.5) * 100) + "%");
        return shortages;
    }
    
    private Map<String, Object> generateDemandVariations(String severity) {
        Map<String, Object> variations = new HashMap<>();
        variations.put("essentialGoodsIncrease", "200-500%");
        variations.put("luxuryGoodsDecrease", "50-80%");
        variations.put("medicalSuppliesIncrease", "1000-2000%");
        return variations;
    }
    
    private List<Map<String, Object>> identifySupplyChainFragilityPoints() {
        return Arrays.asList(
            Map.of("point", "Single Source Suppliers", "risk", "HIGH", "impact", "Complete disruption"),
            Map.of("point", "Key Transportation Hubs", "risk", "MEDIUM", "impact", "Routing delays"),
            Map.of("point", "Critical Component Suppliers", "risk", "HIGH", "impact", "Production stoppage")
        );
    }
    
    // Market data helper methods
    private Map<String, List<Map<String, Object>>> generateCurrencyRateChanges(String region, Duration timeRange, Double volatilityLevel) {
        Map<String, List<Map<String, Object>>> rateChanges = new HashMap<>();
        
        // Generate sample currency pairs relevant to the region
        List<String> currencyPairs = getCurrencyPairsForRegion(region);
        
        for (String pair : currencyPairs) {
            List<Map<String, Object>> changes = new ArrayList<>();
            double baseRate = 1.0;
            double volatility = volatilityLevel != null ? volatilityLevel : CURRENCY_VOLATILITY.get(pair);
            
            // Generate daily rate changes over the time range
            for (int day = 0; day < timeRange.toDays(); day++) {
                double change = random.nextGaussian() * volatility * 0.1; // Daily volatility
                baseRate *= (1 + change);
                
                Map<String, Object> rateChange = new HashMap<>();
                rateChange.put("date", LocalDateTime.now().plusDays(day));
                rateChange.put("rate", BigDecimal.valueOf(baseRate).setScale(4, java.math.RoundingMode.HALF_UP));
                rateChange.put("change", BigDecimal.valueOf(change * 100).setScale(2, java.math.RoundingMode.HALF_UP) + "%");
                changes.add(rateChange);
            }
            
            rateChanges.put(pair, changes);
        }
        
        return rateChanges;
    }
    
    private List<String> getCurrencyPairsForRegion(String region) {
        return switch (region) {
            case "North America" -> Arrays.asList("USD-CAD", "USD-MXN");
            case "Europe" -> Arrays.asList("EUR-USD", "GBP-USD", "EUR-GBP");
            case "Asia" -> Arrays.asList("USD-JPY", "USD-CNY", "USD-INR");
            default -> Arrays.asList("USD-EUR", "USD-JPY", "USD-GBP");
        };
    }
    
    private Map<String, Object> calculateCurrencyImpactOnCosts(Map<String, List<Map<String, Object>>> rateChanges) {
        Map<String, Object> impacts = new HashMap<>();
        impacts.put("averageCostIncrease", "5-15%");
        impacts.put("hedgingRecommendation", "RECOMMENDED");
        impacts.put("riskLevel", "MEDIUM");
        return impacts;
    }
    
    private List<Map<String, Object>> generateCurrencyHedgingScenarios(Map<String, List<Map<String, Object>>> rateChanges) {
        return Arrays.asList(
            Map.of("strategy", "Forward Contracts", "effectiveness", "85%", "cost", "0.5%"),
            Map.of("strategy", "Currency Options", "effectiveness", "70%", "cost", "1.2%"),
            Map.of("strategy", "Natural Hedging", "effectiveness", "60%", "cost", "0%")
        );
    }
    
    // Supplier performance scenario methods
    private Map<String, Object> generatePerformanceDegradationScenario(Supplier supplier, SupplierScenarioRequest request) {
        Map<String, Object> scenario = new HashMap<>();
        scenario.put("scenarioType", "PERFORMANCE_DEGRADATION");
        scenario.put("supplierId", supplier.getId());
        scenario.put("supplierName", supplier.getName());
        
        // Generate gradual quality decline
        List<Map<String, Object>> qualityTrend = generateQualityDegradationTrend(supplier, request.getTimeframe());
        scenario.put("qualityDegradationTrend", qualityTrend);
        
        // Generate warning indicators
        List<Map<String, Object>> warningIndicators = generatePerformanceWarningIndicators(supplier);
        scenario.put("warningIndicators", warningIndicators);
        
        // Root cause analysis
        Map<String, Object> rootCauses = generatePerformanceRootCauses(supplier, request.getSeverity());
        scenario.put("rootCauses", rootCauses);
        
        // Recovery program
        List<Map<String, Object>> recoveryProgram = generatePerformanceRecoveryProgram(supplier);
        scenario.put("recoveryProgram", recoveryProgram);
        
        return scenario;
    }
    
    private Map<String, Object> generateCapacityConstraintScenario(Supplier supplier, SupplierScenarioRequest request) {
        Map<String, Object> scenario = new HashMap<>();
        scenario.put("scenarioType", "CAPACITY_CONSTRAINT");
        scenario.put("supplierId", supplier.getId());
        scenario.put("supplierName", supplier.getName());
        
        // Generate demand surge scenario
        Map<String, Object> demandSurge = generateDemandSurgeScenario(supplier, request.getSeverity());
        scenario.put("demandSurge", demandSurge);
        
        // Capital investment requirements
        Map<String, Object> capitalInvestment = generateCapitalInvestmentRequirements(supplier);
        scenario.put("capitalInvestment", capitalInvestment);
        
        // Lead time extensions
        Map<String, Object> leadTimeExtensions = generateLeadTimeExtensions(supplier, request.getSeverity());
        scenario.put("leadTimeExtensions", leadTimeExtensions);
        
        // Alternative capacity options
        List<Map<String, Object>> alternativeCapacity = generateAlternativeCapacityOptions(supplier);
        scenario.put("alternativeCapacity", alternativeCapacity);
        
        return scenario;
    }
    
    private Map<String, Object> generateQualityIssueScenario(Supplier supplier, SupplierScenarioRequest request) {
        Map<String, Object> scenario = new HashMap<>();
        scenario.put("scenarioType", "QUALITY_ISSUE");
        scenario.put("supplierId", supplier.getId());
        scenario.put("supplierName", supplier.getName());
        
        // Generate product defect scenarios
        List<Map<String, Object>> productDefects = generateProductDefectScenarios(supplier, request.getSeverity());
        scenario.put("productDefects", productDefects);
        
        // Generate recall scenarios
        Map<String, Object> recallScenario = generateRecallScenario(supplier, request.getSeverity());
        scenario.put("recallScenario", recallScenario);
        
        // Customer complaint scenarios
        List<Map<String, Object>> customerComplaints = generateCustomerComplaintScenarios(supplier);
        scenario.put("customerComplaints", customerComplaints);
        
        return scenario;
    }
    
    private Map<String, Object> generateComplianceIssueScenario(Supplier supplier, SupplierScenarioRequest request) {
        Map<String, Object> scenario = new HashMap<>();
        scenario.put("scenarioType", "COMPLIANCE_ISSUE");
        scenario.put("supplierId", supplier.getId());
        scenario.put("supplierName", supplier.getName());
        
        // Certification expiry scenarios
        List<Map<String, Object>> certificationExpiry = generateCertificationExpiryScenarios(supplier);
        scenario.put("certificationExpiry", certificationExpiry);
        
        // Regulatory changes
        Map<String, Object> regulatoryChanges = generateRegulatoryChangeScenarios(supplier);
        scenario.put("regulatoryChanges", regulatoryChanges);
        
        // Audit failure scenarios
        Map<String, Object> auditFailures = generateAuditFailureScenarios(supplier, request.getSeverity());
        scenario.put("auditFailures", auditFailures);
        
        return scenario;
    }
    
    // Additional helper methods for supplier scenarios
    private List<Map<String, Object>> generateQualityDegradationTrend(Supplier supplier, Duration timeframe) {
        List<Map<String, Object>> trend = new ArrayList<>();
        double currentQuality = supplier.getQualityRating().doubleValue();
        double degradationRate = 0.02; // 2% degradation per month
        
        for (int month = 0; month < timeframe.toDays() / 30; month++) {
            currentQuality *= (1 - degradationRate);
            
            Map<String, Object> dataPoint = new HashMap<>();
            dataPoint.put("month", month + 1);
            dataPoint.put("qualityRating", BigDecimal.valueOf(currentQuality).setScale(2, java.math.RoundingMode.HALF_UP));
            dataPoint.put("trendDirection", "DECLINING");
            trend.add(dataPoint);
        }
        
        return trend;
    }
    
    private List<Map<String, Object>> generatePerformanceWarningIndicators(Supplier supplier) {
        return Arrays.asList(
            Map.of("indicator", "Delivery Performance", "currentValue", "92%", "threshold", "95%", "status", "WARNING"),
            Map.of("indicator", "Quality Score", "currentValue", "8.2", "threshold", "8.5", "status", "WARNING"),
            Map.of("indicator", "Response Time", "currentValue", "18 hours", "threshold", "12 hours", "status", "ALERT")
        );
    }
    
    private Map<String, Object> generatePerformanceRootCauses(Supplier supplier, String severity) {
        return Map.of(
            "primaryCause", "Equipment aging and maintenance issues",
            "secondaryCause", "Skilled workforce turnover",
            "contributingFactors", Arrays.asList("Material quality degradation", "Process optimization gaps"),
            "riskLevel", severity
        );
    }
    
    private List<Map<String, Object>> generatePerformanceRecoveryProgram(Supplier supplier) {
        return Arrays.asList(
            Map.of("action", "Equipment Modernization", "timeline", "6 months", "investment", "$250,000"),
            Map.of("action", "Staff Training Program", "timeline", "3 months", "investment", "$50,000"),
            Map.of("action", "Process Optimization", "timeline", "4 months", "investment", "$75,000")
        );
    }
    
    // More helper method implementations...
    private Map<String, Object> generateDemandSurgeScenario(Supplier supplier, String severity) {
        double surgeMultiplier = switch (severity) {
            case "LOW" -> 1.5;
            case "MEDIUM" -> 2.0;
            case "HIGH" -> 3.0;
            default -> 2.0;
        };
        
        return Map.of(
            "demandIncrease", (surgeMultiplier * 100 - 100) + "%",
            "currentCapacity", "100%",
            "requiredCapacity", (surgeMultiplier * 100) + "%",
            "capacityGap", ((surgeMultiplier - 1) * 100) + "%"
        );
    }
    
    private Map<String, Object> generateCapitalInvestmentRequirements(Supplier supplier) {
        return Map.of(
            "equipmentUpgrade", "$500,000",
            "facilityExpansion", "$1,200,000",
            "workforce", "$300,000",
            "totalInvestment", "$2,000,000",
            "paybackPeriod", "18-24 months"
        );
    }
    
    private Map<String, Object> generateLeadTimeExtensions(Supplier supplier, String severity) {
        int extensionWeeks = switch (severity) {
            case "LOW" -> 2;
            case "MEDIUM" -> 4;
            case "HIGH" -> 8;
            default -> 4;
        };
        
        return Map.of(
            "currentLeadTime", "6 weeks",
            "extendedLeadTime", (6 + extensionWeeks) + " weeks",
            "extension", extensionWeeks + " weeks",
            "impactLevel", severity
        );
    }
    
    private List<Map<String, Object>> generateAlternativeCapacityOptions(Supplier supplier) {
        return Arrays.asList(
            Map.of("option", "Contract Manufacturing", "capacity", "50%", "cost", "+15%", "timeline", "2 months"),
            Map.of("option", "Partner Supplier", "capacity", "75%", "cost", "+25%", "timeline", "4 weeks"),
            Map.of("option", "Overtime Production", "capacity", "25%", "cost", "+40%", "timeline", "immediate")
        );
    }
    
    private List<Map<String, Object>> generateProductDefectScenarios(Supplier supplier, String severity) {
        return Arrays.asList(
            Map.of("defectType", "Dimensional Tolerance", "severity", severity, "affectedBatches", "3", "impact", "MODERATE"),
            Map.of("defectType", "Material Composition", "severity", severity, "affectedBatches", "1", "impact", "HIGH"),
            Map.of("defectType", "Surface Finish", "severity", severity, "affectedBatches", "5", "impact", "LOW")
        );
    }
    
    private Map<String, Object> generateRecallScenario(Supplier supplier, String severity) {
        return Map.of(
            "recallTrigger", "Customer safety concern",
            "affectedProducts", "Model XYZ-2023",
            "quantityAffected", "15,000 units",
            "estimatedCost", "$2,500,000",
            "timelineWeeks", "6-8",
            "regualtoryInvolvement", "FDA investigation"
        );
    }
    
    private List<Map<String, Object>> generateCustomerComplaintScenarios(Supplier supplier) {
        return Arrays.asList(
            Map.of("complaint", "Product reliability issues", "frequency", "15/month", "severity", "MEDIUM"),
            Map.of("complaint", "Delivery delays", "frequency", "8/month", "severity", "LOW"),
            Map.of("complaint", "Documentation errors", "frequency", "5/month", "severity", "LOW")
        );
    }
    
    private List<Map<String, Object>> generateCertificationExpiryScenarios(Supplier supplier) {
        return Arrays.asList(
            Map.of("certification", "ISO 9001", "expiryDate", "2024-06-30", "status", "EXPIRING", "renewalCost", "$15,000"),
            Map.of("certification", "FDA Registration", "expiryDate", "2024-12-31", "status", "CURRENT", "renewalCost", "$25,000")
        );
    }
    
    private Map<String, Object> generateRegulatoryChangeScenarios(Supplier supplier) {
        return Map.of(
            "regulation", "New environmental standards",
            "effectiveDate", "2024-09-01",
            "complianceCost", "$150,000",
            "adaptationTime", "6 months",
            "impact", "MEDIUM"
        );
    }
    
    private Map<String, Object> generateAuditFailureScenarios(Supplier supplier, String severity) {
        return Map.of(
            "auditType", "Quality Management System",
            "failureReason", "Documentation gaps and process deviations",
            "correctiveActionPlan", "90-day improvement plan",
            "reinspectionDate", "2024-08-15",
            "riskToSupplyChain", severity
        );
    }
    
    // Placeholder methods for commodity and weather data
    private Map<String, Object> generateCommodityPriceData(MarketDataRequest request) {
        Map<String, Object> commodityData = new HashMap<>();
        commodityData.put("dataType", "COMMODITY");
        commodityData.put("oilPriceVolatility", "±15%");
        commodityData.put("steelPriceIncrease", "+25%");
        commodityData.put("semiconductorShortage", "CRITICAL");
        return commodityData;
    }
    
    private Map<String, Object> generateWeatherImpactData(MarketDataRequest request) {
        Map<String, Object> weatherData = new HashMap<>();
        weatherData.put("dataType", "WEATHER");
        weatherData.put("seasonalPatterns", "Normal summer patterns expected");
        weatherData.put("extremeWeatherRisk", "Elevated hurricane season");
        weatherData.put("climateImpact", "Long-term shipping route changes anticipated");
        return weatherData;
    }
    
    private Map<String, Object> generateEconomicIndicatorData(MarketDataRequest request) {
        Map<String, Object> economicData = new HashMap<>();
        economicData.put("dataType", "ECONOMIC");
        economicData.put("gdpGrowthImpact", "Moderate positive impact on supply chains");
        economicData.put("inflationImpact", "Cost pressures increasing");
        economicData.put("marketConfidence", "STABLE");
        return economicData;
    }
}
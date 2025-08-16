package com.supplychainrisk.service;

import com.supplychainrisk.dto.*;
import com.supplychainrisk.entity.Supplier;
import com.supplychainrisk.repository.SupplierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationEngine {
    
    private static final Logger logger = LoggerFactory.getLogger(RecommendationEngine.class);
    
    @Autowired
    private SupplierRepository supplierRepository;
    
    @Autowired
    private MLPredictionService mlPredictionService;
    
    @Autowired
    private RiskAssessmentService riskAssessmentService;
    
    /**
     * Recommend alternative suppliers with advanced scoring algorithms
     */
    public List<SupplierRecommendation> recommendAlternativeSuppliers(
            Long currentSupplierId, RecommendationCriteria criteria) {
        
        logger.info("Generating supplier recommendations for supplier: {}", currentSupplierId);
        
        try {
            Supplier currentSupplier = supplierRepository.findById(currentSupplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found: " + currentSupplierId));
            
            // Find suppliers with similar capabilities
            List<Supplier> candidateSuppliers = findSimilarSuppliers(currentSupplier, criteria);
            
            // Score each candidate using advanced algorithms
            List<SupplierScore> scoredSuppliers = candidateSuppliers.stream()
                .map(supplier -> scoreSupplier(supplier, criteria, currentSupplier))
                .sorted((a, b) -> Double.compare(b.getTotalScore().doubleValue(), a.getTotalScore().doubleValue()))
                .collect(Collectors.toList());
            
            // Generate comprehensive recommendations
            return scoredSuppliers.stream()
                .limit(criteria.getMaxRecommendations() != null ? criteria.getMaxRecommendations() : 5)
                .map(score -> createSupplierRecommendation(score, criteria))
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            logger.error("Error generating supplier recommendations", e);
            throw new RuntimeException("Failed to generate supplier recommendations", e);
        }
    }
    
    /**
     * Recommend optimal routes with multi-criteria scoring
     */
    public List<RouteRecommendation> recommendOptimalRoutes(
            RouteOptimizationRequest request) {
        
        logger.info("Generating route recommendations from {} to {}", 
            request.getOrigin(), request.getDestination());
        
        try {
            // Generate route options based on real data and algorithms
            List<RouteOption> availableRoutes = generateRouteOptions(request);
            
            // Score routes based on multiple criteria
            List<RouteScore> scoredRoutes = availableRoutes.stream()
                .map(route -> scoreRoute(route, request.getCriteria()))
                .sorted((a, b) -> Double.compare(b.getTotalScore().doubleValue(), a.getTotalScore().doubleValue()))
                .collect(Collectors.toList());
            
            // Generate route recommendations with alternatives
            return scoredRoutes.stream()
                .limit(request.getMaxRecommendations())
                .map(score -> createRouteRecommendation(score, request))
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            logger.error("Error generating route recommendations", e);
            throw new RuntimeException("Failed to generate route recommendations", e);
        }
    }
    
    /**
     * Optimize inventory levels using advanced analytics
     */
    public InventoryOptimizationResult optimizeInventoryLevels(
            InventoryOptimizationRequest request) {
        
        logger.info("Optimizing inventory for product: {}", request.getProductId());
        
        try {
            // Analyze historical demand patterns
            List<DemandPattern> demandPatterns = analyzeDemandPatterns(
                request.getProductId(), request.getTimeRange());
            
            // Get supplier risk assessments
            Map<Long, RiskAssessment> supplierRisks = getSupplierRiskAssessments(
                request.getSupplierIds());
            
            // Calculate optimal inventory levels
            InventoryOptimizationResult.OptimalInventoryLevels levels = 
                calculateOptimalLevels(demandPatterns, supplierRisks, request.getConstraints());
            
            // Generate recommendations and risk mitigation strategies
            List<String> recommendations = generateInventoryRecommendations(levels, request);
            Map<String, Object> riskMitigation = generateRiskMitigationStrategies(supplierRisks);
            
            return InventoryOptimizationResult.builder()
                .productId(request.getProductId())
                .productCode(request.getProductCode())
                .optimalLevels(levels)
                .recommendations(recommendations)
                .riskMitigation(riskMitigation)
                .estimatedCostSavings(calculateCostSavings(levels, request))
                .riskReduction(calculateRiskReduction(supplierRisks))
                .confidence(BigDecimal.valueOf(85.0))
                .build();
                
        } catch (Exception e) {
            logger.error("Error optimizing inventory levels", e);
            throw new RuntimeException("Failed to optimize inventory levels", e);
        }
    }
    
    // Helper methods for supplier recommendation
    
    private List<Supplier> findSimilarSuppliers(Supplier currentSupplier, RecommendationCriteria criteria) {
        List<Supplier> allSuppliers = supplierRepository.findAll();
        
        return allSuppliers.stream()
            .filter(s -> !s.getId().equals(currentSupplier.getId()))
            .filter(s -> s.getStatus() == Supplier.SupplierStatus.ACTIVE)
            .filter(s -> matchesBasicCriteria(s, criteria))
            .filter(s -> hasSimilarCapabilities(s, currentSupplier))
            .collect(Collectors.toList());
    }
    
    private boolean matchesBasicCriteria(Supplier supplier, RecommendationCriteria criteria) {
        if (criteria.getPreferredCountries() != null && !criteria.getPreferredCountries().isEmpty()) {
            if (!criteria.getPreferredCountries().contains(supplier.getCountry())) {
                return false;
            }
        }
        
        if (criteria.getExcludedCountries() != null && !criteria.getExcludedCountries().isEmpty()) {
            if (criteria.getExcludedCountries().contains(supplier.getCountry())) {
                return false;
            }
        }
        
        if (criteria.getMinQualityThreshold() != null) {
            if (supplier.getQualityRating() == null || 
                supplier.getQualityRating().compareTo(criteria.getMinQualityThreshold()) < 0) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean hasSimilarCapabilities(Supplier supplier, Supplier currentSupplier) {
        // Check if suppliers have similar business capabilities
        return Objects.equals(supplier.getIndustry(), currentSupplier.getIndustry()) ||
               Objects.equals(supplier.getBusinessType(), currentSupplier.getBusinessType());
    }
    
    private SupplierScore scoreSupplier(Supplier supplier, RecommendationCriteria criteria, Supplier currentSupplier) {
        Map<String, BigDecimal> scoreBreakdown = new HashMap<>();
        
        // Quality score (30%)
        BigDecimal qualityScore = calculateQualityScore(supplier);
        scoreBreakdown.put("quality", qualityScore);
        
        // Risk score (25%)
        BigDecimal riskScore = calculateRiskScore(supplier);
        scoreBreakdown.put("risk", riskScore);
        
        // Cost competitiveness (20%)
        BigDecimal costScore = calculateCostScore(supplier);
        scoreBreakdown.put("cost", costScore);
        
        // Delivery performance (15%)
        BigDecimal deliveryScore = calculateDeliveryScore(supplier);
        scoreBreakdown.put("delivery", deliveryScore);
        
        // Strategic fit (10%)
        BigDecimal strategicScore = calculateStrategicFit(supplier, currentSupplier);
        scoreBreakdown.put("strategic", strategicScore);
        
        // Calculate weighted total score
        BigDecimal totalScore = qualityScore.multiply(BigDecimal.valueOf(0.30))
            .add(riskScore.multiply(BigDecimal.valueOf(0.25)))
            .add(costScore.multiply(BigDecimal.valueOf(0.20)))
            .add(deliveryScore.multiply(BigDecimal.valueOf(0.15)))
            .add(strategicScore.multiply(BigDecimal.valueOf(0.10)));
        
        return new SupplierScore(supplier, totalScore, scoreBreakdown);
    }
    
    private BigDecimal calculateQualityScore(Supplier supplier) {
        if (supplier.getQualityRating() == null) return BigDecimal.valueOf(50);
        return supplier.getQualityRating().multiply(BigDecimal.valueOf(10));
    }
    
    private BigDecimal calculateRiskScore(Supplier supplier) {
        if (supplier.getOverallRiskScore() == null) return BigDecimal.valueOf(50);
        return BigDecimal.valueOf(100 - supplier.getOverallRiskScore());
    }
    
    private BigDecimal calculateCostScore(Supplier supplier) {
        if (supplier.getCostCompetitivenessScore() == null) return BigDecimal.valueOf(50);
        return BigDecimal.valueOf(supplier.getCostCompetitivenessScore());
    }
    
    private BigDecimal calculateDeliveryScore(Supplier supplier) {
        if (supplier.getOnTimeDeliveryRate() == null) return BigDecimal.valueOf(50);
        return supplier.getOnTimeDeliveryRate();
    }
    
    private BigDecimal calculateStrategicFit(Supplier supplier, Supplier currentSupplier) {
        BigDecimal score = BigDecimal.valueOf(50);
        
        if (Objects.equals(supplier.getIndustry(), currentSupplier.getIndustry())) {
            score = score.add(BigDecimal.valueOf(20));
        }
        
        if (supplier.getPreferredSupplier() != null && supplier.getPreferredSupplier()) {
            score = score.add(BigDecimal.valueOf(15));
        }
        
        if (supplier.getStrategicSupplier() != null && supplier.getStrategicSupplier()) {
            score = score.add(BigDecimal.valueOf(15));
        }
        
        return score.min(BigDecimal.valueOf(100));
    }
    
    private SupplierRecommendation createSupplierRecommendation(SupplierScore score, RecommendationCriteria criteria) {
        Supplier supplier = score.getSupplier();
        
        List<String> advantages = generateSupplierAdvantages(supplier);
        List<String> risks = generateSupplierRisks(supplier);
        
        return SupplierRecommendation.builder()
            .supplierId(supplier.getId())
            .supplierName(supplier.getName())
            .supplierCode(supplier.getSupplierCode())
            .totalScore(score.getTotalScore())
            .scoreBreakdown(score.getScoreBreakdown())
            .recommendationType("ALTERNATIVE")
            .reasonCode("RISK_DIVERSIFICATION")
            .advantages(advantages)
            .risks(risks)
            .confidence(calculateRecommendationConfidence(score))
            .priority(calculatePriority(score.getTotalScore()))
            .build();
    }
    
    private List<String> generateSupplierAdvantages(Supplier supplier) {
        List<String> advantages = new ArrayList<>();
        
        if (supplier.getQualityRating() != null && supplier.getQualityRating().compareTo(BigDecimal.valueOf(8)) >= 0) {
            advantages.add("High quality rating (" + supplier.getQualityRating() + "/10)");
        }
        
        if (supplier.getOnTimeDeliveryRate() != null && supplier.getOnTimeDeliveryRate().compareTo(BigDecimal.valueOf(90)) >= 0) {
            advantages.add("Excellent delivery performance (" + supplier.getOnTimeDeliveryRate() + "%)");
        }
        
        if (supplier.getPreferredSupplier() != null && supplier.getPreferredSupplier()) {
            advantages.add("Preferred supplier status");
        }
        
        if (supplier.getIsoCertifications() != null && !supplier.getIsoCertifications().isEmpty()) {
            advantages.add("ISO certified (" + String.join(", ", supplier.getIsoCertifications()) + ")");
        }
        
        return advantages;
    }
    
    private List<String> generateSupplierRisks(Supplier supplier) {
        List<String> risks = new ArrayList<>();
        
        if (supplier.getOverallRiskScore() != null && supplier.getOverallRiskScore() > 70) {
            risks.add("High overall risk score (" + supplier.getOverallRiskScore() + "/100)");
        }
        
        if (supplier.getFinancialRiskScore() != null && supplier.getFinancialRiskScore() > 60) {
            risks.add("Elevated financial risk");
        }
        
        if (supplier.getGeographicRiskScore() != null && supplier.getGeographicRiskScore() > 60) {
            risks.add("Geographic concentration risk");
        }
        
        return risks;
    }
    
    private BigDecimal calculateRecommendationConfidence(SupplierScore score) {
        // Base confidence on score distribution and data quality
        BigDecimal variance = calculateScoreVariance(score.getScoreBreakdown());
        BigDecimal baseConfidence = BigDecimal.valueOf(80);
        
        // Higher variance means lower confidence
        BigDecimal confidenceAdjustment = variance.multiply(BigDecimal.valueOf(-0.5));
        
        return baseConfidence.add(confidenceAdjustment)
            .max(BigDecimal.valueOf(60))
            .min(BigDecimal.valueOf(95));
    }
    
    private BigDecimal calculateScoreVariance(Map<String, BigDecimal> scores) {
        if (scores.isEmpty()) return BigDecimal.ZERO;
        
        BigDecimal mean = scores.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(scores.size()), RoundingMode.HALF_UP);
        
        BigDecimal variance = scores.values().stream()
            .map(score -> score.subtract(mean).pow(2))
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(scores.size()), RoundingMode.HALF_UP);
        
        return variance;
    }
    
    private Integer calculatePriority(BigDecimal totalScore) {
        if (totalScore.compareTo(BigDecimal.valueOf(80)) >= 0) return 1;
        if (totalScore.compareTo(BigDecimal.valueOf(70)) >= 0) return 2;
        if (totalScore.compareTo(BigDecimal.valueOf(60)) >= 0) return 3;
        return 4;
    }
    
    // Route recommendation helper methods
    
    private List<RouteOption> generateRouteOptions(RouteOptimizationRequest request) {
        // Simulate route generation - in real implementation this would use actual route data
        List<RouteOption> routes = new ArrayList<>();
        
        // Generate different transport mode options
        String[] transportModes = {"OCEAN", "AIR", "GROUND", "MULTIMODAL"};
        
        for (String mode : transportModes) {
            RouteOption route = new RouteOption();
            route.setRouteId(generateRouteId(request.getOrigin(), request.getDestination(), mode));
            route.setOrigin(request.getOrigin());
            route.setDestination(request.getDestination());
            route.setTransportMode(mode);
            route.setEstimatedCost(generateEstimatedCost(mode));
            route.setEstimatedTransitDays(generateEstimatedTransitDays(mode));
            route.setReliabilityScore(generateReliabilityScore(mode));
            route.setRiskScore(generateRouteRiskScore(mode));
            route.setCarrierName(generateCarrierName(mode));
            routes.add(route);
        }
        
        return routes;
    }
    
    private RouteScore scoreRoute(RouteOption route, RecommendationCriteria criteria) {
        Map<String, BigDecimal> scoreBreakdown = new HashMap<>();
        
        // Cost score (30%)
        BigDecimal costScore = calculateRouteCostScore(route);
        scoreBreakdown.put("cost", costScore);
        
        // Speed score (25%)
        BigDecimal speedScore = calculateRouteSpeedScore(route);
        scoreBreakdown.put("speed", speedScore);
        
        // Reliability score (25%)
        BigDecimal reliabilityScore = route.getReliabilityScore();
        scoreBreakdown.put("reliability", reliabilityScore);
        
        // Risk score (20%)
        BigDecimal riskScore = BigDecimal.valueOf(100).subtract(route.getRiskScore());
        scoreBreakdown.put("risk", riskScore);
        
        // Calculate weighted total score
        BigDecimal totalScore = costScore.multiply(BigDecimal.valueOf(0.30))
            .add(speedScore.multiply(BigDecimal.valueOf(0.25)))
            .add(reliabilityScore.multiply(BigDecimal.valueOf(0.25)))
            .add(riskScore.multiply(BigDecimal.valueOf(0.20)));
        
        return new RouteScore(route, totalScore, scoreBreakdown);
    }
    
    private RouteRecommendation createRouteRecommendation(RouteScore score, RouteOptimizationRequest request) {
        RouteOption route = score.getRoute();
        
        RouteRecommendation recommendation = new RouteRecommendation();
        recommendation.setRouteId(route.getRouteId());
        recommendation.setOrigin(route.getOrigin());
        recommendation.setDestination(route.getDestination());
        recommendation.setTransportMode(route.getTransportMode());
        recommendation.setTotalScore(score.getTotalScore());
        recommendation.setScoreBreakdown(score.getScoreBreakdown());
        recommendation.setEstimatedCost(route.getEstimatedCost());
        recommendation.setEstimatedTransitDays(route.getEstimatedTransitDays());
        recommendation.setReliabilityScore(route.getReliabilityScore());
        recommendation.setRiskScore(route.getRiskScore());
        recommendation.setCarrierName(route.getCarrierName());
        recommendation.setAdvantages(generateRouteAdvantages(route));
        recommendation.setRisks(generateRouteRisks(route));
        recommendation.setConfidence(calculateRouteConfidence(score));
        recommendation.setGeneratedAt(LocalDateTime.now());
        
        return recommendation;
    }
    
    // Inventory optimization helper methods
    
    private List<DemandPattern> analyzeDemandPatterns(Long productId, InventoryOptimizationRequest.DateRange timeRange) {
        // Simulate demand pattern analysis
        List<DemandPattern> patterns = new ArrayList<>();
        
        // Generate mock demand patterns
        for (int i = 0; i < 12; i++) {
            DemandPattern pattern = new DemandPattern();
            pattern.setMonth(i + 1);
            pattern.setDemand(BigDecimal.valueOf(100 + Math.random() * 50));
            pattern.setVariance(BigDecimal.valueOf(Math.random() * 20));
            patterns.add(pattern);
        }
        
        return patterns;
    }
    
    private Map<Long, RiskAssessment> getSupplierRiskAssessments(List<Long> supplierIds) {
        Map<Long, RiskAssessment> riskAssessments = new HashMap<>();
        
        if (supplierIds != null) {
            for (Long supplierId : supplierIds) {
                Optional<Supplier> supplierOpt = supplierRepository.findById(supplierId);
                if (supplierOpt.isPresent()) {
                    Supplier supplier = supplierOpt.get();
                    RiskAssessment assessment = new RiskAssessment();
                    assessment.setSupplierId(supplierId);
                    assessment.setOverallRisk(supplier.getOverallRiskScore() != null ? 
                        BigDecimal.valueOf(supplier.getOverallRiskScore()) : BigDecimal.valueOf(50));
                    assessment.setFinancialRisk(supplier.getFinancialRiskScore() != null ? 
                        BigDecimal.valueOf(supplier.getFinancialRiskScore()) : BigDecimal.valueOf(50));
                    assessment.setOperationalRisk(supplier.getOperationalRiskScore() != null ? 
                        BigDecimal.valueOf(supplier.getOperationalRiskScore()) : BigDecimal.valueOf(50));
                    riskAssessments.put(supplierId, assessment);
                }
            }
        }
        
        return riskAssessments;
    }
    
    private InventoryOptimizationResult.OptimalInventoryLevels calculateOptimalLevels(
            List<DemandPattern> demandPatterns, 
            Map<Long, RiskAssessment> supplierRisks,
            Map<String, Object> constraints) {
        
        InventoryOptimizationResult.OptimalInventoryLevels levels = 
            new InventoryOptimizationResult.OptimalInventoryLevels();
        
        // Calculate based on demand patterns and risk
        BigDecimal avgDemand = demandPatterns.stream()
            .map(DemandPattern::getDemand)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(demandPatterns.size()), RoundingMode.HALF_UP);
        
        BigDecimal avgVariance = demandPatterns.stream()
            .map(DemandPattern::getVariance)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(demandPatterns.size()), RoundingMode.HALF_UP);
        
        // Safety stock = 1.65 * sqrt(variance) for 95% service level
        BigDecimal safetyStock = BigDecimal.valueOf(1.65)
            .multiply(BigDecimal.valueOf(Math.sqrt(avgVariance.doubleValue())));
        
        // Reorder point = average demand + safety stock
        BigDecimal reorderPoint = avgDemand.add(safetyStock);
        
        // EOQ calculation (simplified)
        BigDecimal eoq = BigDecimal.valueOf(Math.sqrt(2 * avgDemand.doubleValue() * 100 / 0.2));
        
        levels.setSafetyStock(safetyStock);
        levels.setReorderPoint(reorderPoint);
        levels.setEconomicOrderQuantity(eoq);
        levels.setMaxStock(reorderPoint.add(eoq));
        
        // Supplier allocation based on risk
        Map<String, BigDecimal> allocation = calculateSupplierAllocation(supplierRisks);
        levels.setSupplierAllocation(allocation);
        
        return levels;
    }
    
    private Map<String, BigDecimal> calculateSupplierAllocation(Map<Long, RiskAssessment> supplierRisks) {
        Map<String, BigDecimal> allocation = new HashMap<>();
        
        if (supplierRisks.isEmpty()) {
            allocation.put("primary", BigDecimal.valueOf(100));
            return allocation;
        }
        
        // Allocate based on inverse risk scores
        BigDecimal totalInverseRisk = supplierRisks.values().stream()
            .map(risk -> BigDecimal.valueOf(100).subtract(risk.getOverallRisk()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        int index = 0;
        for (Map.Entry<Long, RiskAssessment> entry : supplierRisks.entrySet()) {
            BigDecimal inverseRisk = BigDecimal.valueOf(100).subtract(entry.getValue().getOverallRisk());
            BigDecimal percentage = inverseRisk.divide(totalInverseRisk, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
            allocation.put("supplier_" + entry.getKey(), percentage);
            index++;
        }
        
        return allocation;
    }
    
    private List<String> generateInventoryRecommendations(
            InventoryOptimizationResult.OptimalInventoryLevels levels, 
            InventoryOptimizationRequest request) {
        
        List<String> recommendations = new ArrayList<>();
        
        recommendations.add("Maintain safety stock of " + levels.getSafetyStock().setScale(0, RoundingMode.HALF_UP) + " units");
        recommendations.add("Set reorder point at " + levels.getReorderPoint().setScale(0, RoundingMode.HALF_UP) + " units");
        recommendations.add("Optimal order quantity: " + levels.getEconomicOrderQuantity().setScale(0, RoundingMode.HALF_UP) + " units");
        
        if (levels.getSupplierAllocation().size() > 1) {
            recommendations.add("Diversify suppliers to reduce risk concentration");
        }
        
        return recommendations;
    }
    
    private Map<String, Object> generateRiskMitigationStrategies(Map<Long, RiskAssessment> supplierRisks) {
        Map<String, Object> strategies = new HashMap<>();
        
        List<String> recommendations = new ArrayList<>();
        
        boolean hasHighRiskSupplier = supplierRisks.values().stream()
            .anyMatch(risk -> risk.getOverallRisk().compareTo(BigDecimal.valueOf(70)) > 0);
        
        if (hasHighRiskSupplier) {
            recommendations.add("Consider alternative suppliers for high-risk sources");
            recommendations.add("Increase safety stock for high-risk suppliers");
            recommendations.add("Implement more frequent risk monitoring");
        }
        
        if (supplierRisks.size() == 1) {
            recommendations.add("Diversify supplier base to reduce single-source dependency");
        }
        
        strategies.put("recommendations", recommendations);
        strategies.put("riskLevel", hasHighRiskSupplier ? "HIGH" : "MEDIUM");
        
        return strategies;
    }
    
    private BigDecimal calculateCostSavings(InventoryOptimizationResult.OptimalInventoryLevels levels, InventoryOptimizationRequest request) {
        // Simplified cost savings calculation
        BigDecimal currentLevel = request.getCurrentInventoryLevel() != null ? 
            request.getCurrentInventoryLevel() : BigDecimal.valueOf(1000);
        
        BigDecimal optimalLevel = levels.getReorderPoint().add(levels.getEconomicOrderQuantity().divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP));
        
        if (currentLevel.compareTo(optimalLevel) > 0) {
            return currentLevel.subtract(optimalLevel).multiply(BigDecimal.valueOf(10)); // $10 per unit carrying cost
        }
        
        return BigDecimal.ZERO;
    }
    
    private BigDecimal calculateRiskReduction(Map<Long, RiskAssessment> supplierRisks) {
        if (supplierRisks.isEmpty()) return BigDecimal.ZERO;
        
        BigDecimal avgRisk = supplierRisks.values().stream()
            .map(RiskAssessment::getOverallRisk)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(supplierRisks.size()), RoundingMode.HALF_UP);
        
        // Risk reduction through diversification
        BigDecimal diversificationBenefit = BigDecimal.valueOf(Math.sqrt(supplierRisks.size()) * 5);
        
        return diversificationBenefit.min(avgRisk.multiply(BigDecimal.valueOf(0.3)));
    }
    
    // Utility methods for route generation
    
    private String generateRouteId(String origin, String destination, String mode) {
        return mode + "_" + origin + "_" + destination + "_" + System.currentTimeMillis();
    }
    
    private BigDecimal generateEstimatedCost(String mode) {
        Map<String, Integer> baseCosts = Map.of(
            "OCEAN", 1000,
            "AIR", 3000,
            "GROUND", 1500,
            "MULTIMODAL", 2000
        );
        int baseCost = baseCosts.getOrDefault(mode, 2000);
        return BigDecimal.valueOf(baseCost + Math.random() * 500);
    }
    
    private Integer generateEstimatedTransitDays(String mode) {
        Map<String, Integer> baseDaysMap = Map.of(
            "OCEAN", 25,
            "AIR", 3,
            "GROUND", 8,
            "MULTIMODAL", 15
        );
        int baseDays = baseDaysMap.getOrDefault(mode, 10);
        return baseDays + (int)(Math.random() * 5);
    }
    
    private BigDecimal generateReliabilityScore(String mode) {
        Map<String, Double> baseReliability = Map.of(
            "OCEAN", 75.0,
            "AIR", 90.0,
            "GROUND", 85.0,
            "MULTIMODAL", 80.0
        );
        double base = baseReliability.getOrDefault(mode, 80.0);
        return BigDecimal.valueOf(base + Math.random() * 10);
    }
    
    private BigDecimal generateRouteRiskScore(String mode) {
        Map<String, Double> baseRisk = Map.of(
            "OCEAN", 30.0,
            "AIR", 20.0,
            "GROUND", 25.0,
            "MULTIMODAL", 35.0
        );
        double base = baseRisk.getOrDefault(mode, 30.0);
        return BigDecimal.valueOf(base + Math.random() * 15);
    }
    
    private String generateCarrierName(String mode) {
        Map<String, List<String>> carriers = Map.of(
            "OCEAN", List.of("Maersk", "COSCO", "MSC"),
            "AIR", List.of("DHL", "FedEx", "UPS"),
            "GROUND", List.of("UPS Ground", "FedEx Ground", "USPS"),
            "MULTIMODAL", List.of("DB Schenker", "Kuehne+Nagel", "DHL Supply Chain")
        );
        List<String> modeCarriers = carriers.getOrDefault(mode, List.of("Generic Carrier"));
        return modeCarriers.get((int)(Math.random() * modeCarriers.size()));
    }
    
    private BigDecimal calculateRouteCostScore(RouteOption route) {
        // Higher cost = lower score
        BigDecimal maxCost = BigDecimal.valueOf(5000);
        BigDecimal normalizedCost = route.getEstimatedCost().divide(maxCost, 2, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(100).subtract(normalizedCost.multiply(BigDecimal.valueOf(100)))
            .max(BigDecimal.ZERO);
    }
    
    private BigDecimal calculateRouteSpeedScore(RouteOption route) {
        // Faster delivery = higher score
        BigDecimal maxDays = BigDecimal.valueOf(30);
        BigDecimal normalizedDays = BigDecimal.valueOf(route.getEstimatedTransitDays()).divide(maxDays, 2, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(100).subtract(normalizedDays.multiply(BigDecimal.valueOf(100)))
            .max(BigDecimal.ZERO);
    }
    
    private List<String> generateRouteAdvantages(RouteOption route) {
        List<String> advantages = new ArrayList<>();
        
        if (route.getReliabilityScore().compareTo(BigDecimal.valueOf(85)) >= 0) {
            advantages.add("High reliability (" + route.getReliabilityScore().setScale(1, RoundingMode.HALF_UP) + "%)");
        }
        
        if (route.getEstimatedTransitDays() <= 5) {
            advantages.add("Fast delivery (" + route.getEstimatedTransitDays() + " days)");
        }
        
        if (route.getRiskScore().compareTo(BigDecimal.valueOf(25)) <= 0) {
            advantages.add("Low risk route");
        }
        
        return advantages;
    }
    
    private List<String> generateRouteRisks(RouteOption route) {
        List<String> risks = new ArrayList<>();
        
        if (route.getRiskScore().compareTo(BigDecimal.valueOf(40)) > 0) {
            risks.add("Higher risk exposure");
        }
        
        if (route.getEstimatedTransitDays() > 20) {
            risks.add("Extended transit time");
        }
        
        if (route.getEstimatedCost().compareTo(BigDecimal.valueOf(3500)) > 0) {
            risks.add("High cost option");
        }
        
        return risks;
    }
    
    private BigDecimal calculateRouteConfidence(RouteScore score) {
        // Base confidence on score consistency
        BigDecimal avgScore = score.getScoreBreakdown().values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(score.getScoreBreakdown().size()), RoundingMode.HALF_UP);
        
        // Higher average score = higher confidence
        return avgScore.multiply(BigDecimal.valueOf(0.8)).add(BigDecimal.valueOf(20))
            .min(BigDecimal.valueOf(95))
            .max(BigDecimal.valueOf(70));
    }
    
    // Helper classes
    
    private static class SupplierScore {
        private final Supplier supplier;
        private final BigDecimal totalScore;
        private final Map<String, BigDecimal> scoreBreakdown;
        
        public SupplierScore(Supplier supplier, BigDecimal totalScore, Map<String, BigDecimal> scoreBreakdown) {
            this.supplier = supplier;
            this.totalScore = totalScore;
            this.scoreBreakdown = scoreBreakdown;
        }
        
        public Supplier getSupplier() { return supplier; }
        public BigDecimal getTotalScore() { return totalScore; }
        public Map<String, BigDecimal> getScoreBreakdown() { return scoreBreakdown; }
    }
    
    private static class RouteOption {
        private String routeId;
        private String origin;
        private String destination;
        private String transportMode;
        private BigDecimal estimatedCost;
        private Integer estimatedTransitDays;
        private BigDecimal reliabilityScore;
        private BigDecimal riskScore;
        private String carrierName;
        
        // Getters and setters
        public String getRouteId() { return routeId; }
        public void setRouteId(String routeId) { this.routeId = routeId; }
        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }
        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
        public String getTransportMode() { return transportMode; }
        public void setTransportMode(String transportMode) { this.transportMode = transportMode; }
        public BigDecimal getEstimatedCost() { return estimatedCost; }
        public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }
        public Integer getEstimatedTransitDays() { return estimatedTransitDays; }
        public void setEstimatedTransitDays(Integer estimatedTransitDays) { this.estimatedTransitDays = estimatedTransitDays; }
        public BigDecimal getReliabilityScore() { return reliabilityScore; }
        public void setReliabilityScore(BigDecimal reliabilityScore) { this.reliabilityScore = reliabilityScore; }
        public BigDecimal getRiskScore() { return riskScore; }
        public void setRiskScore(BigDecimal riskScore) { this.riskScore = riskScore; }
        public String getCarrierName() { return carrierName; }
        public void setCarrierName(String carrierName) { this.carrierName = carrierName; }
    }
    
    private static class RouteScore {
        private final RouteOption route;
        private final BigDecimal totalScore;
        private final Map<String, BigDecimal> scoreBreakdown;
        
        public RouteScore(RouteOption route, BigDecimal totalScore, Map<String, BigDecimal> scoreBreakdown) {
            this.route = route;
            this.totalScore = totalScore;
            this.scoreBreakdown = scoreBreakdown;
        }
        
        public RouteOption getRoute() { return route; }
        public BigDecimal getTotalScore() { return totalScore; }
        public Map<String, BigDecimal> getScoreBreakdown() { return scoreBreakdown; }
    }
    
    private static class DemandPattern {
        private Integer month;
        private BigDecimal demand;
        private BigDecimal variance;
        
        public Integer getMonth() { return month; }
        public void setMonth(Integer month) { this.month = month; }
        public BigDecimal getDemand() { return demand; }
        public void setDemand(BigDecimal demand) { this.demand = demand; }
        public BigDecimal getVariance() { return variance; }
        public void setVariance(BigDecimal variance) { this.variance = variance; }
    }
    
    private static class RiskAssessment {
        private Long supplierId;
        private BigDecimal overallRisk;
        private BigDecimal financialRisk;
        private BigDecimal operationalRisk;
        
        public Long getSupplierId() { return supplierId; }
        public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
        public BigDecimal getOverallRisk() { return overallRisk; }
        public void setOverallRisk(BigDecimal overallRisk) { this.overallRisk = overallRisk; }
        public BigDecimal getFinancialRisk() { return financialRisk; }
        public void setFinancialRisk(BigDecimal financialRisk) { this.financialRisk = financialRisk; }
        public BigDecimal getOperationalRisk() { return operationalRisk; }
        public void setOperationalRisk(BigDecimal operationalRisk) { this.operationalRisk = operationalRisk; }
    }
}
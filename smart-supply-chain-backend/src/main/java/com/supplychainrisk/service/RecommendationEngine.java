package com.supplychainrisk.service;

import com.supplychainrisk.dto.SupplierRecommendation;
import com.supplychainrisk.dto.RecommendationCriteria;
import com.supplychainrisk.entity.Supplier;
import com.supplychainrisk.repository.SupplierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationEngine {
    
    private static final Logger logger = LoggerFactory.getLogger(RecommendationEngine.class);
    
    @Autowired
    private SupplierRepository supplierRepository;
    
    @Autowired
    private RiskAssessmentService riskAssessmentService;
    
    @Autowired
    private MLPredictionService mlPredictionService;
    
    // Default scoring weights
    private static final Map<String, BigDecimal> DEFAULT_WEIGHTS = Map.of(
        "quality", BigDecimal.valueOf(0.30),
        "cost", BigDecimal.valueOf(0.25),
        "risk", BigDecimal.valueOf(0.25),
        "delivery", BigDecimal.valueOf(0.20)
    );
    
    /**
     * Recommend alternative suppliers based on specified criteria and ML-enhanced scoring
     */
    public List<SupplierRecommendation> recommendAlternativeSuppliers(
            Long currentSupplierId, RecommendationCriteria criteria) {
        
        logger.info("Generating supplier recommendations for current supplier: {}", currentSupplierId);
        
        try {
            Supplier currentSupplier = null;
            if (currentSupplierId != null) {
                currentSupplier = supplierRepository.findById(currentSupplierId)
                    .orElse(null);
            }
            
            // Find candidate suppliers based on criteria
            List<Supplier> candidateSuppliers = findCandidateSuppliers(currentSupplier, criteria);
            
            // Score and rank suppliers
            final Supplier finalCurrentSupplier = currentSupplier; // Make it effectively final
            List<SupplierScore> scoredSuppliers = candidateSuppliers.stream()
                .map(supplier -> scoreSupplier(supplier, criteria, finalCurrentSupplier))
                .filter(Objects::nonNull)
                .sorted((a, b) -> b.getTotalScore().compareTo(a.getTotalScore()))
                .collect(Collectors.toList());
            
            // Generate recommendations
            int maxRecommendations = criteria.getMaxRecommendations() != null ? 
                criteria.getMaxRecommendations() : 5;
            
            return scoredSuppliers.stream()
                .limit(maxRecommendations)
                .map(score -> createSupplierRecommendation(score, criteria))
                .collect(Collectors.toList());
            
        } catch (Exception e) {
            logger.error("Error generating supplier recommendations: {}", e.getMessage(), e);
            throw new RecommendationException("Failed to generate supplier recommendations", e);
        }
    }
    
    /**
     * Recommend optimal routes based on criteria (simplified implementation)
     */
    public List<Map<String, Object>> recommendOptimalRoutes(
            String origin, String destination, Map<String, Object> criteria) {
        
        logger.info("Generating route recommendations from {} to {}", origin, destination);
        
        List<Map<String, Object>> routeRecommendations = new ArrayList<>();
        
        // Simplified route generation (in a real implementation, this would integrate with
        // shipping APIs, geographic databases, and route optimization algorithms)
        String[] transportModes = {"OCEAN", "AIR", "GROUND", "MULTIMODAL"};
        
        for (String mode : transportModes) {
            Map<String, Object> route = generateRouteRecommendation(origin, destination, mode, criteria);
            routeRecommendations.add(route);
        }
        
        // Sort by total score
        routeRecommendations.sort((a, b) -> {
            BigDecimal scoreA = (BigDecimal) a.getOrDefault("totalScore", BigDecimal.ZERO);
            BigDecimal scoreB = (BigDecimal) b.getOrDefault("totalScore", BigDecimal.ZERO);
            return scoreB.compareTo(scoreA);
        });
        
        return routeRecommendations;
    }
    
    /**
     * Generate inventory optimization recommendations
     */
    public Map<String, Object> recommendInventoryOptimization(
            List<Long> supplierIds, Map<String, Object> criteria) {
        
        logger.info("Generating inventory optimization recommendations for {} suppliers", supplierIds.size());
        
        Map<String, Object> recommendations = new HashMap<>();
        
        for (Long supplierId : supplierIds) {
            Supplier supplier = supplierRepository.findById(supplierId).orElse(null);
            if (supplier != null) {
                Map<String, Object> supplierInventoryRec = generateInventoryRecommendation(supplier, criteria);
                recommendations.put("supplier_" + supplierId, supplierInventoryRec);
            }
        }
        
        // Add aggregate recommendations
        recommendations.put("aggregate_recommendations", generateAggregateInventoryRecommendations(supplierIds, criteria));
        
        return recommendations;
    }
    
    /**
     * Find candidate suppliers based on criteria
     */
    private List<Supplier> findCandidateSuppliers(Supplier currentSupplier, RecommendationCriteria criteria) {
        List<Supplier> allSuppliers = supplierRepository.findAll();
        
        return allSuppliers.stream()
            .filter(supplier -> {
                // Exclude current supplier
                if (currentSupplier != null && supplier.getId().equals(currentSupplier.getId())) {
                    return false;
                }
                
                // Filter by status
                if (supplier.getStatus() != Supplier.SupplierStatus.ACTIVE) {
                    return false;
                }
                
                // Apply risk threshold filter
                if (criteria.getMaxRiskThreshold() != null) {
                    if (supplier.getOverallRiskScore() > criteria.getMaxRiskThreshold().intValue()) {
                        return false;
                    }
                }
                
                // Apply quality threshold filter
                if (criteria.getMinQualityThreshold() != null) {
                    if (supplier.getQualityRating() == null || 
                        supplier.getQualityRating().compareTo(criteria.getMinQualityThreshold()) < 0) {
                        return false;
                    }
                }
                
                // Apply country filters
                if (criteria.getExcludedCountries() != null && 
                    criteria.getExcludedCountries().contains(supplier.getCountry())) {
                    return false;
                }
                
                // Apply certification requirements
                if (criteria.getRequiredCertifications() != null && !criteria.getRequiredCertifications().isEmpty()) {
                    List<String> supplierCerts = supplier.getIsoCertifications();
                    if (supplierCerts == null || 
                        !supplierCerts.containsAll(criteria.getRequiredCertifications())) {
                        return false;
                    }
                }
                
                return true;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Score a supplier based on criteria
     */
    private SupplierScore scoreSupplier(Supplier supplier, RecommendationCriteria criteria, 
                                       Supplier currentSupplier) {
        try {
            SupplierScore score = new SupplierScore();
            score.supplier = supplier;
            
            // Get weights from criteria or use defaults
            Map<String, BigDecimal> weights = Optional.ofNullable(criteria.getWeights())
                .orElse(DEFAULT_WEIGHTS);
            
            // Calculate individual scores
            BigDecimal qualityScore = calculateQualityScore(supplier);
            BigDecimal costScore = calculateCostScore(supplier, currentSupplier);
            BigDecimal riskScore = calculateRiskScore(supplier);
            BigDecimal deliveryScore = calculateDeliveryScore(supplier);
            
            // Store individual scores
            Map<String, BigDecimal> scoreBreakdown = new HashMap<>();
            scoreBreakdown.put("quality", qualityScore);
            scoreBreakdown.put("cost", costScore);
            scoreBreakdown.put("risk", riskScore);
            scoreBreakdown.put("delivery", deliveryScore);
            score.scoreBreakdown = scoreBreakdown;
            
            // Calculate weighted total score
            BigDecimal totalScore = qualityScore.multiply(weights.get("quality"))
                .add(costScore.multiply(weights.get("cost")))
                .add(riskScore.multiply(weights.get("risk")))
                .add(deliveryScore.multiply(weights.get("delivery")));
            
            score.totalScore = totalScore.setScale(2, RoundingMode.HALF_UP);
            
            // Calculate confidence based on data completeness
            score.confidence = calculateScoreConfidence(supplier);
            
            return score;
            
        } catch (Exception e) {
            logger.warn("Error scoring supplier {}: {}", supplier.getId(), e.getMessage());
            return null;
        }
    }
    
    /**
     * Calculate quality score (0-100)
     */
    private BigDecimal calculateQualityScore(Supplier supplier) {
        BigDecimal score = BigDecimal.ZERO;
        
        // Quality rating (0-10 scale)
        if (supplier.getQualityRating() != null) {
            score = supplier.getQualityRating().multiply(BigDecimal.valueOf(10)); // Convert to 0-100 scale
        } else {
            score = BigDecimal.valueOf(50); // Default middle score
        }
        
        // Certification bonus
        if (supplier.getIsoCertifications() != null) {
            int certCount = supplier.getIsoCertifications().size();
            score = score.add(BigDecimal.valueOf(Math.min(20, certCount * 5))); // Up to 20 points for certs
        }
        
        return score.min(BigDecimal.valueOf(100));
    }
    
    /**
     * Calculate cost competitiveness score (0-100, higher is better value)
     */
    private BigDecimal calculateCostScore(Supplier supplier, Supplier currentSupplier) {
        BigDecimal score = BigDecimal.valueOf(50); // Default middle score
        
        if (supplier.getCostCompetitivenessScore() != null) {
            score = BigDecimal.valueOf(supplier.getCostCompetitivenessScore());
        }
        
        // Compare with current supplier if available
        if (currentSupplier != null && currentSupplier.getCostCompetitivenessScore() != null) {
            int comparison = Integer.compare(supplier.getCostCompetitivenessScore(), 
                                           currentSupplier.getCostCompetitivenessScore());
            if (comparison > 0) {
                score = score.add(BigDecimal.valueOf(10)); // Bonus for better cost
            } else if (comparison < 0) {
                score = score.subtract(BigDecimal.valueOf(10)); // Penalty for worse cost
            }
        }
        
        return score.max(BigDecimal.ZERO).min(BigDecimal.valueOf(100));
    }
    
    /**
     * Calculate risk score (0-100, higher score means lower risk)
     */
    private BigDecimal calculateRiskScore(Supplier supplier) {
        // Invert risk score so higher score means lower risk
        int overallRisk = supplier.getOverallRiskScore();
        return BigDecimal.valueOf(100 - overallRisk);
    }
    
    /**
     * Calculate delivery performance score (0-100)
     */
    private BigDecimal calculateDeliveryScore(Supplier supplier) {
        if (supplier.getOnTimeDeliveryRate() != null) {
            return supplier.getOnTimeDeliveryRate();
        }
        
        // Default score based on responsiveness if delivery rate not available
        if (supplier.getResponsivenessScore() != null) {
            return BigDecimal.valueOf(supplier.getResponsivenessScore());
        }
        
        return BigDecimal.valueOf(50); // Default middle score
    }
    
    /**
     * Calculate confidence in the scoring based on data completeness
     */
    private BigDecimal calculateScoreConfidence(Supplier supplier) {
        int dataPoints = 0;
        int availableDataPoints = 0;
        
        // Check availability of key data points
        if (supplier.getQualityRating() != null) availableDataPoints++;
        dataPoints++;
        
        if (supplier.getOnTimeDeliveryRate() != null) availableDataPoints++;
        dataPoints++;
        
        if (supplier.getCostCompetitivenessScore() != null) availableDataPoints++;
        dataPoints++;
        
        if (supplier.getOverallRiskScore() != null) availableDataPoints++;
        dataPoints++;
        
        if (supplier.getAnnualRevenue() != null) availableDataPoints++;
        dataPoints++;
        
        if (supplier.getEmployeeCount() != null) availableDataPoints++;
        dataPoints++;
        
        double completeness = (double) availableDataPoints / dataPoints;
        return BigDecimal.valueOf(completeness * 100).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Create supplier recommendation from score
     */
    private SupplierRecommendation createSupplierRecommendation(SupplierScore score, 
                                                               RecommendationCriteria criteria) {
        
        return SupplierRecommendation.builder()
            .supplierId(score.supplier.getId())
            .supplierName(score.supplier.getName())
            .supplierCode(score.supplier.getSupplierCode())
            .totalScore(score.totalScore)
            .scoreBreakdown(score.scoreBreakdown)
            .recommendationType(determineRecommendationType(score, criteria))
            .reasonCode(generateReasonCode(score, criteria))
            .advantages(generateAdvantages(score.supplier))
            .risks(generateRisks(score.supplier))
            .comparisonMetrics(generateComparisonMetrics(score.supplier))
            .confidence(score.confidence)
            .priority(calculatePriority(score))
            .build();
    }
    
    /**
     * Generate route recommendation
     */
    private Map<String, Object> generateRouteRecommendation(String origin, String destination, 
                                                          String transportMode, Map<String, Object> criteria) {
        Map<String, Object> route = new HashMap<>();
        
        route.put("routeId", UUID.randomUUID().toString());
        route.put("origin", origin);
        route.put("destination", destination);
        route.put("transportMode", transportMode);
        
        // Simplified scoring based on transport mode characteristics
        Map<String, BigDecimal> scoreBreakdown = new HashMap<>();
        BigDecimal totalScore;
        
        switch (transportMode) {
            case "OCEAN":
                scoreBreakdown.put("cost", BigDecimal.valueOf(90));
                scoreBreakdown.put("speed", BigDecimal.valueOf(30));
                scoreBreakdown.put("reliability", BigDecimal.valueOf(80));
                scoreBreakdown.put("risk", BigDecimal.valueOf(70));
                route.put("estimatedTransitDays", 14);
                route.put("estimatedCost", BigDecimal.valueOf(2500));
                break;
            case "AIR":
                scoreBreakdown.put("cost", BigDecimal.valueOf(40));
                scoreBreakdown.put("speed", BigDecimal.valueOf(95));
                scoreBreakdown.put("reliability", BigDecimal.valueOf(90));
                scoreBreakdown.put("risk", BigDecimal.valueOf(85));
                route.put("estimatedTransitDays", 2);
                route.put("estimatedCost", BigDecimal.valueOf(8500));
                break;
            case "GROUND":
                scoreBreakdown.put("cost", BigDecimal.valueOf(70));
                scoreBreakdown.put("speed", BigDecimal.valueOf(60));
                scoreBreakdown.put("reliability", BigDecimal.valueOf(85));
                scoreBreakdown.put("risk", BigDecimal.valueOf(80));
                route.put("estimatedTransitDays", 5);
                route.put("estimatedCost", BigDecimal.valueOf(3200));
                break;
            default: // MULTIMODAL
                scoreBreakdown.put("cost", BigDecimal.valueOf(75));
                scoreBreakdown.put("speed", BigDecimal.valueOf(70));
                scoreBreakdown.put("reliability", BigDecimal.valueOf(75));
                scoreBreakdown.put("risk", BigDecimal.valueOf(75));
                route.put("estimatedTransitDays", 8);
                route.put("estimatedCost", BigDecimal.valueOf(4200));
        }
        
        // Calculate weighted total score
        totalScore = scoreBreakdown.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP);
        
        route.put("scoreBreakdown", scoreBreakdown);
        route.put("totalScore", totalScore);
        route.put("advantages", generateRouteAdvantages(transportMode));
        route.put("risks", generateRouteRisks(transportMode));
        route.put("confidence", BigDecimal.valueOf(85)); // Simplified confidence
        
        return route;
    }
    
    /**
     * Generate inventory recommendation for a supplier
     */
    private Map<String, Object> generateInventoryRecommendation(Supplier supplier, Map<String, Object> criteria) {
        Map<String, Object> recommendation = new HashMap<>();
        
        // Base safety stock calculation (simplified)
        int riskBasedBuffer = Math.max(0, supplier.getOverallRiskScore() - 50);
        BigDecimal deliveryRate = supplier.getOnTimeDeliveryRate() != null ? 
            supplier.getOnTimeDeliveryRate() : BigDecimal.valueOf(80);
        
        // Calculate recommended safety stock days
        int safetyStockDays = 14 + (riskBasedBuffer / 10); // Base 14 days + risk adjustment
        if (deliveryRate.compareTo(BigDecimal.valueOf(90)) < 0) {
            safetyStockDays += 7; // Add extra days for poor delivery performance
        }
        
        recommendation.put("safetyStockDays", safetyStockDays);
        recommendation.put("reorderPoint", calculateReorderPoint(supplier, safetyStockDays));
        recommendation.put("bufferStrategy", determineBufferStrategy(supplier));
        recommendation.put("monitoringFrequency", determineMonitoringFrequency(supplier));
        
        return recommendation;
    }
    
    // Helper methods for generating recommendation details
    
    private String determineRecommendationType(SupplierScore score, RecommendationCriteria criteria) {
        if (score.totalScore.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return "STRATEGIC_PARTNER";
        } else if (score.totalScore.compareTo(BigDecimal.valueOf(60)) >= 0) {
            return "ALTERNATIVE";
        } else {
            return "BACKUP";
        }
    }
    
    private String generateReasonCode(SupplierScore score, RecommendationCriteria criteria) {
        BigDecimal highestScore = score.scoreBreakdown.values().stream()
            .max(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);
        
        String reason = score.scoreBreakdown.entrySet().stream()
            .filter(entry -> entry.getValue().equals(highestScore))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse("balanced");
        
        return "STRONG_" + reason.toUpperCase() + "_PERFORMANCE";
    }
    
    private List<String> generateAdvantages(Supplier supplier) {
        List<String> advantages = new ArrayList<>();
        
        if (supplier.getQualityRating() != null && supplier.getQualityRating().compareTo(BigDecimal.valueOf(8)) >= 0) {
            advantages.add("Excellent quality rating (" + supplier.getQualityRating() + "/10)");
        }
        
        if (supplier.getOnTimeDeliveryRate() != null && supplier.getOnTimeDeliveryRate().compareTo(BigDecimal.valueOf(90)) >= 0) {
            advantages.add("Strong delivery performance (" + supplier.getOnTimeDeliveryRate() + "%)");
        }
        
        if (supplier.getOverallRiskScore() <= 30) {
            advantages.add("Low overall risk profile");
        }
        
        if (supplier.getIsoCertifications() != null && supplier.getIsoCertifications().size() >= 3) {
            advantages.add("Comprehensive certifications (" + supplier.getIsoCertifications().size() + " ISO certs)");
        }
        
        return advantages;
    }
    
    private List<String> generateRisks(Supplier supplier) {
        List<String> risks = new ArrayList<>();
        
        if (supplier.getOverallRiskScore() > 60) {
            risks.add("High overall risk score (" + supplier.getOverallRiskScore() + ")");
        }
        
        if (supplier.getOnTimeDeliveryRate() != null && supplier.getOnTimeDeliveryRate().compareTo(BigDecimal.valueOf(80)) < 0) {
            risks.add("Below-average delivery performance");
        }
        
        if (supplier.getFinancialRiskScore() > 50) {
            risks.add("Elevated financial risk");
        }
        
        return risks;
    }
    
    private Map<String, Object> generateComparisonMetrics(Supplier supplier) {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("overallRisk", supplier.getOverallRiskScore());
        metrics.put("qualityRating", supplier.getQualityRating());
        metrics.put("deliveryRate", supplier.getOnTimeDeliveryRate());
        metrics.put("costCompetitiveness", supplier.getCostCompetitivenessScore());
        metrics.put("employeeCount", supplier.getEmployeeCount());
        metrics.put("yearsInBusiness", supplier.getYearsInBusiness());
        return metrics;
    }
    
    private Integer calculatePriority(SupplierScore score) {
        // Priority 1-10, lower is higher priority
        if (score.totalScore.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return 1;
        } else if (score.totalScore.compareTo(BigDecimal.valueOf(60)) >= 0) {
            return 3;
        } else if (score.totalScore.compareTo(BigDecimal.valueOf(40)) >= 0) {
            return 5;
        } else {
            return 8;
        }
    }
    
    private List<String> generateRouteAdvantages(String transportMode) {
        return switch (transportMode) {
            case "OCEAN" -> Arrays.asList("Lowest cost option", "Suitable for large volumes", "Environmentally friendly");
            case "AIR" -> Arrays.asList("Fastest delivery", "High reliability", "Global reach");
            case "GROUND" -> Arrays.asList("Good cost-speed balance", "Door-to-door service", "Real-time tracking");
            default -> Arrays.asList("Flexible routing", "Cost-effective", "Risk distribution");
        };
    }
    
    private List<String> generateRouteRisks(String transportMode) {
        return switch (transportMode) {
            case "OCEAN" -> Arrays.asList("Weather delays", "Port congestion", "Long transit time");
            case "AIR" -> Arrays.asList("High cost", "Weight restrictions", "Weather sensitivity");
            case "GROUND" -> Arrays.asList("Traffic delays", "Geographic limitations", "Border crossing issues");
            default -> Arrays.asList("Complexity coordination", "Multiple handoffs", "Variable timing");
        };
    }
    
    private String calculateReorderPoint(Supplier supplier, int safetyStockDays) {
        // Simplified reorder point calculation
        BigDecimal deliveryRate = supplier.getOnTimeDeliveryRate() != null ? 
            supplier.getOnTimeDeliveryRate() : BigDecimal.valueOf(80);
        
        // Average lead time adjustment based on delivery performance
        int leadTimeDays = deliveryRate.compareTo(BigDecimal.valueOf(90)) >= 0 ? 7 : 10;
        
        return String.format("%d days lead time + %d days safety stock", leadTimeDays, safetyStockDays);
    }
    
    private String determineBufferStrategy(Supplier supplier) {
        if (supplier.getOverallRiskScore() > 70) {
            return "HIGH_BUFFER";
        } else if (supplier.getOverallRiskScore() > 40) {
            return "MODERATE_BUFFER";
        } else {
            return "LEAN_BUFFER";
        }
    }
    
    private String determineMonitoringFrequency(Supplier supplier) {
        if (supplier.getOverallRiskScore() > 60) {
            return "DAILY";
        } else if (supplier.getOverallRiskScore() > 30) {
            return "WEEKLY";
        } else {
            return "MONTHLY";
        }
    }
    
    private Map<String, Object> generateAggregateInventoryRecommendations(List<Long> supplierIds, Map<String, Object> criteria) {
        Map<String, Object> aggregateRec = new HashMap<>();
        
        List<Supplier> suppliers = supplierRepository.findAllById(supplierIds);
        
        // Calculate average risk across suppliers
        double avgRisk = suppliers.stream()
            .mapToInt(Supplier::getOverallRiskScore)
            .average()
            .orElse(50.0);
        
        aggregateRec.put("averageSupplierRisk", avgRisk);
        aggregateRec.put("recommendedDiversification", avgRisk > 50 ? "HIGH" : "MODERATE");
        aggregateRec.put("portfolioRebalancing", avgRisk > 60 ? "REQUIRED" : "OPTIONAL");
        
        return aggregateRec;
    }
    
    // Inner class for scoring
    private static class SupplierScore {
        Supplier supplier;
        BigDecimal totalScore;
        Map<String, BigDecimal> scoreBreakdown;
        BigDecimal confidence;
        
        public BigDecimal getTotalScore() {
            return totalScore;
        }
    }
    
    // Custom exception for recommendation engine errors
    public static class RecommendationException extends RuntimeException {
        public RecommendationException(String message) {
            super(message);
        }
        
        public RecommendationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
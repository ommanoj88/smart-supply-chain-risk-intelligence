package com.supplychainrisk.service;

import com.supplychainrisk.dto.*;
import com.supplychainrisk.entity.*;
import com.supplychainrisk.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EnhancedSupplierService {
    
    private static final Logger logger = LoggerFactory.getLogger(EnhancedSupplierService.class);
    
    @Autowired
    private SupplierRepository supplierRepository;
    
    @Autowired
    private SupplierLocationRepository supplierLocationRepository;
    
    @Autowired
    private RiskFactorRepository riskFactorRepository;
    
    @Autowired
    private SupplierCertificationRepository supplierCertificationRepository;
    
    @Autowired
    private RiskAssessmentService riskAssessmentService;
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @Autowired
    private SupplierIntelligenceService supplierIntelligenceService;
    
    public SupplierProfile getComprehensiveSupplierProfile(Long supplierId) {
        try {
            Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found: " + supplierId));
            
            // Get current risk assessment
            SupplierRiskAssessment currentRisk = getCurrentRiskAssessment(supplierId);
            
            // Get performance analytics
            SupplierPerformanceAnalytics performance = getPerformanceAnalytics(
                supplierId, SupplierPerformanceAnalytics.TimeRange.LAST_12_MONTHS);
            
            // Get market intelligence
            MarketIntelligence marketData = supplierIntelligenceService.getMarketIntelligence(supplier);
            
            // Get supplier relationships
            SupplierNetwork network = getSupplierNetwork(supplier);
            
            return SupplierProfile.builder()
                .supplier(supplier)
                .riskAssessment(currentRisk)
                .performanceAnalytics(performance)
                .marketIntelligence(marketData)
                .supplierNetwork(network)
                .build();
                
        } catch (Exception e) {
            logger.error("Error retrieving supplier profile for ID {}", supplierId, e);
            throw new RuntimeException("Failed to retrieve supplier profile", e);
        }
    }
    
    public SupplierRiskAssessment updateSupplierRiskScore(Long supplierId, Map<String, Object> riskFactors) {
        try {
            Supplier supplier = getSupplierById(supplierId);
            
            // Calculate new risk score
            RiskCalculationResult result = calculateRiskScore(supplier, riskFactors);
            
            // Update supplier risk information
            supplier.setRiskScore(result.getRiskScore());
            supplier.setRiskLevel(result.getRiskLevel());
            supplier.setRiskLastUpdated(LocalDateTime.now());
            
            // Update risk factors
            updateRiskFactors(supplier, result.getRiskFactors());
            
            // Save changes
            supplier = supplierRepository.save(supplier);
            
            // Generate alerts if risk level changed significantly
            checkRiskAlerts(supplier, result);
            
            return SupplierRiskAssessment.builder()
                .supplier(supplier)
                .riskScore(result.getRiskScore())
                .riskLevel(result.getRiskLevel())
                .riskFactors(result.getRiskFactors())
                .recommendations(result.getRecommendations())
                .build();
                
        } catch (Exception e) {
            logger.error("Error updating risk assessment for supplier {}", supplierId, e);
            throw new RuntimeException("Failed to update supplier risk score", e);
        }
    }
    
    public SupplierPerformanceAnalytics getSupplierPerformanceDashboard(Long supplierId, 
                                                                       SupplierPerformanceAnalytics.TimeRange timeRange) {
        try {
            Supplier supplier = getSupplierById(supplierId);
            
            // Get performance metrics
            SupplierPerformanceAnalytics.PerformanceMetrics metrics = calculatePerformanceMetrics(supplier, timeRange);
            
            // Get trend analysis
            List<SupplierPerformanceAnalytics.PerformanceTrend> trends = getPerformanceTrends(supplier, timeRange);
            
            // Get benchmarking data
            SupplierPerformanceAnalytics.BenchmarkData benchmarks = getBenchmarkData(supplier, timeRange);
            
            // Get improvement recommendations
            List<SupplierPerformanceAnalytics.ImprovementRecommendation> recommendations = 
                getImprovementRecommendations(supplier, metrics);
            
            SupplierPerformanceAnalytics dashboard = new SupplierPerformanceAnalytics();
            dashboard.setSupplierId(supplierId);
            dashboard.setSupplierName(supplier.getName());
            dashboard.setCurrentMetrics(metrics);
            dashboard.setTrends(trends);
            dashboard.setBenchmarks(benchmarks);
            dashboard.setRecommendations(recommendations);
            dashboard.setTimeRange(timeRange);
            
            return dashboard;
            
        } catch (Exception e) {
            logger.error("Error retrieving performance dashboard for supplier {}", supplierId, e);
            throw new RuntimeException("Failed to retrieve supplier performance dashboard", e);
        }
    }
    
    public List<SupplierRecommendation> findAlternativeSuppliers(SupplierRequirements requirements) {
        try {
            // Search suppliers based on capabilities
            List<Supplier> candidates = supplierRepository.findAll().stream()
                .filter(supplier -> matchesRequirements(supplier, requirements))
                .collect(Collectors.toList());
            
            // Score and rank suppliers
            return candidates.stream()
                .map(supplier -> scoreSupplier(supplier, requirements))
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(requirements.getMaxResults() != null ? requirements.getMaxResults() : 10)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            logger.error("Error discovering suppliers", e);
            throw new RuntimeException("Failed to discover suppliers", e);
        }
    }
    
    public Map<String, Object> getSupplierRiskDistribution() {
        try {
            List<Supplier> allSuppliers = supplierRepository.findAll();
            
            Map<String, Object> distribution = new HashMap<>();
            Map<String, Long> riskLevelCounts = allSuppliers.stream()
                .collect(Collectors.groupingBy(
                    supplier -> supplier.getRiskLevel() != null ? 
                        supplier.getRiskLevel().toString() : "UNKNOWN",
                    Collectors.counting()
                ));
            
            distribution.put("riskLevelDistribution", riskLevelCounts);
            distribution.put("totalSuppliers", allSuppliers.size());
            distribution.put("averageRiskScore", 
                allSuppliers.stream()
                    .filter(s -> s.getRiskScore() != null)
                    .mapToDouble(Supplier::getRiskScore)
                    .average().orElse(0.0));
            
            return distribution;
            
        } catch (Exception e) {
            logger.error("Error retrieving risk distribution", e);
            throw new RuntimeException("Failed to retrieve risk distribution", e);
        }
    }
    
    public Map<String, Object> getSupplierPerformanceTrends(SupplierPerformanceAnalytics.TimeRange timeRange) {
        try {
            List<Supplier> allSuppliers = supplierRepository.findAll();
            
            Map<String, Object> trends = new HashMap<>();
            trends.put("averageOnTimeDelivery", 
                allSuppliers.stream()
                    .filter(s -> s.getOnTimeDeliveryRate() != null)
                    .mapToDouble(s -> s.getOnTimeDeliveryRate().doubleValue())
                    .average().orElse(0.0));
            
            trends.put("averageQualityRating", 
                allSuppliers.stream()
                    .filter(s -> s.getQualityRating() != null)
                    .mapToDouble(s -> s.getQualityRating().doubleValue())
                    .average().orElse(0.0));
            
            trends.put("timeRange", timeRange.toString());
            trends.put("generatedAt", LocalDateTime.now());
            
            return trends;
            
        } catch (Exception e) {
            logger.error("Error retrieving performance trends", e);
            throw new RuntimeException("Failed to retrieve performance trends", e);
        }
    }
    
    // Helper methods
    private Supplier getSupplierById(Long supplierId) {
        return supplierRepository.findById(supplierId)
            .orElseThrow(() -> new RuntimeException("Supplier not found: " + supplierId));
    }
    
    private SupplierRiskAssessment getCurrentRiskAssessment(Long supplierId) {
        Supplier supplier = getSupplierById(supplierId);
        List<RiskFactor> riskFactors = riskFactorRepository.findBySupplierIdAndIsActive(supplierId, true);
        
        return SupplierRiskAssessment.builder()
            .supplier(supplier)
            .riskScore(supplier.getRiskScore())
            .riskLevel(supplier.getRiskLevel())
            .riskFactors(riskFactors)
            .recommendations(riskAssessmentService.getRiskRecommendations(supplier))
            .build();
    }
    
    private SupplierPerformanceAnalytics getPerformanceAnalytics(Long supplierId, 
                                                               SupplierPerformanceAnalytics.TimeRange timeRange) {
        Supplier supplier = getSupplierById(supplierId);
        
        SupplierPerformanceAnalytics analytics = new SupplierPerformanceAnalytics();
        analytics.setSupplierId(supplierId);
        analytics.setSupplierName(supplier.getName());
        analytics.setCurrentMetrics(calculatePerformanceMetrics(supplier, timeRange));
        analytics.setTimeRange(timeRange);
        
        return analytics;
    }
    
    private SupplierNetwork getSupplierNetwork(Supplier supplier) {
        SupplierNetwork network = new SupplierNetwork();
        network.setMainSupplier(supplier);
        network.setParentSupplier(supplier.getParentSupplier());
        
        if (supplier.getSubsidiaries() != null) {
            network.setSubsidiaries(new ArrayList<>(supplier.getSubsidiaries()));
        }
        
        return network;
    }
    
    private RiskCalculationResult calculateRiskScore(Supplier supplier, Map<String, Object> riskFactors) {
        // Simplified risk calculation
        double calculatedRisk = supplier.getOverallRiskScore() != null ? 
            supplier.getOverallRiskScore().doubleValue() : 50.0;
        
        Supplier.RiskLevel riskLevel = determineRiskLevel(calculatedRisk);
        
        RiskCalculationResult result = new RiskCalculationResult();
        result.setRiskScore(calculatedRisk);
        result.setRiskLevel(riskLevel);
        result.setRiskFactors(new ArrayList<>());
        result.setRecommendations(riskAssessmentService.getRiskRecommendations(supplier));
        
        return result;
    }
    
    private Supplier.RiskLevel determineRiskLevel(double riskScore) {
        if (riskScore <= 20) return Supplier.RiskLevel.VERY_LOW;
        if (riskScore <= 40) return Supplier.RiskLevel.LOW;
        if (riskScore <= 60) return Supplier.RiskLevel.MEDIUM;
        if (riskScore <= 80) return Supplier.RiskLevel.HIGH;
        return Supplier.RiskLevel.VERY_HIGH;
    }
    
    private void updateRiskFactors(Supplier supplier, List<RiskFactor> riskFactors) {
        // Implementation for updating risk factors
        logger.info("Updating risk factors for supplier {}", supplier.getId());
    }
    
    private void checkRiskAlerts(Supplier supplier, RiskCalculationResult result) {
        // Implementation for generating risk alerts
        logger.info("Checking risk alerts for supplier {}", supplier.getId());
    }
    
    private SupplierPerformanceAnalytics.PerformanceMetrics calculatePerformanceMetrics(Supplier supplier, 
                                                                                       SupplierPerformanceAnalytics.TimeRange timeRange) {
        SupplierPerformanceAnalytics.PerformanceMetrics metrics = new SupplierPerformanceAnalytics.PerformanceMetrics();
        metrics.setOnTimeDeliveryRate(supplier.getOnTimeDeliveryRate() != null ? 
            supplier.getOnTimeDeliveryRate().doubleValue() : 0.0);
        metrics.setQualityScore(supplier.getQualityRating() != null ? 
            supplier.getQualityRating().doubleValue() : 0.0);
        metrics.setCostCompetitiveness(supplier.getCostCompetitivenessScore() != null ? 
            supplier.getCostCompetitivenessScore().doubleValue() : 0.0);
        metrics.setResponsiveness(supplier.getResponsivenessScore() != null ? 
            supplier.getResponsivenessScore().doubleValue() : 0.0);
        metrics.setLastCalculated(LocalDateTime.now());
        
        return metrics;
    }
    
    private List<SupplierPerformanceAnalytics.PerformanceTrend> getPerformanceTrends(Supplier supplier, 
                                                                                    SupplierPerformanceAnalytics.TimeRange timeRange) {
        // Simplified implementation
        return new ArrayList<>();
    }
    
    private SupplierPerformanceAnalytics.BenchmarkData getBenchmarkData(Supplier supplier, 
                                                                       SupplierPerformanceAnalytics.TimeRange timeRange) {
        // Simplified implementation
        return new SupplierPerformanceAnalytics.BenchmarkData();
    }
    
    private List<SupplierPerformanceAnalytics.ImprovementRecommendation> getImprovementRecommendations(
            Supplier supplier, SupplierPerformanceAnalytics.PerformanceMetrics metrics) {
        // Simplified implementation
        return new ArrayList<>();
    }
    
    private boolean matchesRequirements(Supplier supplier, SupplierRequirements requirements) {
        // Simplified matching logic
        return supplier.getStatus() == Supplier.SupplierStatus.ACTIVE;
    }
    
    private SupplierRecommendation scoreSupplier(Supplier supplier, SupplierRequirements requirements) {
        // Simplified scoring logic
        SupplierRecommendation recommendation = new SupplierRecommendation();
        recommendation.setSupplier(supplier);
        recommendation.setScore(75.0); // Default score
        recommendation.setMatchReason("Meets basic requirements");
        
        return recommendation;
    }
    
    // Inner class for risk calculation result
    private static class RiskCalculationResult {
        private Double riskScore;
        private Supplier.RiskLevel riskLevel;
        private List<RiskFactor> riskFactors;
        private List<String> recommendations;
        
        // Getters and Setters
        public Double getRiskScore() { return riskScore; }
        public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }
        
        public Supplier.RiskLevel getRiskLevel() { return riskLevel; }
        public void setRiskLevel(Supplier.RiskLevel riskLevel) { this.riskLevel = riskLevel; }
        
        public List<RiskFactor> getRiskFactors() { return riskFactors; }
        public void setRiskFactors(List<RiskFactor> riskFactors) { this.riskFactors = riskFactors; }
        
        public List<String> getRecommendations() { return recommendations; }
        public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    }
}
package com.supplychainrisk.service;

import com.supplychainrisk.dto.AnalyticsRequest;
import com.supplychainrisk.dto.MLPredictionResult;
import com.supplychainrisk.dto.RiskPrediction;
import com.supplychainrisk.entity.AnalyticsResult;
import com.supplychainrisk.entity.Supplier;
import com.supplychainrisk.repository.AnalyticsResultRepository;
import com.supplychainrisk.repository.SupplierRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AnalyticsService {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);
    
    @Autowired
    private MLPredictionService mlPredictionService;
    
    @Autowired
    private RiskAssessmentService riskAssessmentService;
    
    @Autowired
    private SupplierRepository supplierRepository;
    
    @Autowired
    private AnalyticsResultRepository analyticsResultRepository;
    
    @Autowired
    private RealTimeUpdateService realTimeUpdateService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Generate comprehensive predictive analytics combining historical data with ML predictions
     */
    public AnalyticsResult generatePredictiveAnalytics(AnalyticsRequest request) {
        logger.info("Generating predictive analytics for request: {}", request.getAnalysisType());
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Collect historical data based on request parameters
            List<Map<String, Object>> historicalData = collectHistoricalData(request);
            
            // Generate ML predictions
            Duration predictionHorizon = Duration.ofDays(
                Optional.ofNullable(request.getTimeHorizonDays()).orElse(30)
            );
            MLPredictionResult predictions = mlPredictionService.predict(historicalData, predictionHorizon);
            
            // Calculate enhanced risk scores
            Map<String, BigDecimal> riskScores = calculateEnhancedRiskScores(request, predictions);
            
            // Generate intelligent recommendations
            List<Map<String, Object>> recommendations = generateIntelligentRecommendations(
                predictions, riskScores, request.getBusinessContext()
            );
            
            // Create and save analytics result
            AnalyticsResult result = createAnalyticsResult(
                request, predictions, riskScores, recommendations, startTime
            );
            
            // Broadcast real-time update
            broadcastAnalyticsUpdate(result);
            
            return result;
            
        } catch (Exception e) {
            logger.error("Error generating predictive analytics: {}", e.getMessage(), e);
            throw new AnalyticsException("Failed to generate predictive analytics", e);
        }
    }
    
    /**
     * Assess real-time risk for a specific supplier with ML-enhanced predictions
     */
    public Map<String, Object> assessRealTimeRisk(Long supplierId, Map<String, Object> context) {
        logger.info("Assessing real-time risk for supplier: {}", supplierId);
        
        try {
            Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found: " + supplierId));
            
            // Collect real-time data for the supplier
            Map<String, Object> realTimeData = collectRealTimeSupplierData(supplier, context);
            
            // Calculate current risk using existing assessment
            Map<String, Integer> currentRisk = calculateCurrentRiskScores(supplier);
            
            // Predict future risk using ML
            RiskPrediction futureRisk = mlPredictionService.predictRisk(
                supplier, realTimeData, Duration.ofDays(30)
            );
            
            // Generate risk alerts if necessary
            List<Map<String, Object>> alerts = generateRiskAlerts(supplier, currentRisk, futureRisk);
            
            // Create comprehensive risk assessment result
            Map<String, Object> assessment = new HashMap<>();
            assessment.put("supplierId", supplierId);
            assessment.put("supplierName", supplier.getName());
            assessment.put("currentRisk", currentRisk);
            assessment.put("predictedRisk", futureRisk);
            assessment.put("riskTrend", calculateRiskTrend(currentRisk, futureRisk));
            assessment.put("alerts", alerts);
            assessment.put("confidence", futureRisk.getConfidence());
            assessment.put("lastUpdated", LocalDateTime.now());
            assessment.put("recommendations", riskAssessmentService.getRiskRecommendations(supplier));
            
            // Broadcast risk update
            realTimeUpdateService.broadcastSupplierUpdate(
                supplierId, "RISK_ASSESSMENT", assessment
            );
            
            return assessment;
            
        } catch (Exception e) {
            logger.error("Error assessing real-time risk for supplier {}: {}", supplierId, e.getMessage(), e);
            throw new AnalyticsException("Failed to assess real-time risk", e);
        }
    }
    
    /**
     * Get analytics performance metrics and insights
     */
    public Map<String, Object> getAnalyticsPerformanceMetrics() {
        logger.debug("Retrieving analytics performance metrics");
        
        Map<String, Object> metrics = new HashMap<>();
        
        // Get recent analytics results for performance analysis
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        List<AnalyticsResult> recentResults = analyticsResultRepository.findRecentResults(since);
        
        // Calculate performance metrics
        metrics.put("totalAnalytics", recentResults.size());
        metrics.put("averageProcessingTime", calculateAverageProcessingTime(recentResults));
        metrics.put("averageConfidence", calculateAverageConfidence(recentResults));
        metrics.put("analyticsBreakdown", getAnalyticsBreakdownByType(recentResults));
        metrics.put("performanceTrends", calculatePerformanceTrends(recentResults));
        
        // Model performance metrics
        Map<String, Object> modelMetrics = new HashMap<>();
        Set<String> modelVersions = recentResults.stream()
            .map(AnalyticsResult::getModelVersion)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        
        for (String version : modelVersions) {
            Double avgAccuracy = analyticsResultRepository.getAverageAccuracyByModelVersion(version);
            modelMetrics.put(version + "_accuracy", avgAccuracy != null ? avgAccuracy : 0.0);
        }
        metrics.put("modelPerformance", modelMetrics);
        
        return metrics;
    }
    
    /**
     * Collect historical data based on analytics request parameters
     */
    private List<Map<String, Object>> collectHistoricalData(AnalyticsRequest request) {
        List<Map<String, Object>> historicalData = new ArrayList<>();
        
        // Determine date range for historical data collection
        LocalDateTime endDate = Optional.ofNullable(request.getEndDate()).orElse(LocalDateTime.now());
        LocalDateTime startDate = Optional.ofNullable(request.getStartDate())
            .orElse(endDate.minusDays(90)); // Default to 90 days of history
        
        // Collect supplier-specific data if supplier IDs are provided
        if (request.getSupplierIds() != null && !request.getSupplierIds().isEmpty()) {
            for (Long supplierId : request.getSupplierIds()) {
                Supplier supplier = supplierRepository.findById(supplierId).orElse(null);
                if (supplier != null) {
                    Map<String, Object> supplierData = createSupplierDataPoint(supplier, endDate);
                    historicalData.add(supplierData);
                }
            }
        } else {
            // Collect aggregated data for all suppliers
            List<Supplier> suppliers = supplierRepository.findAll();
            for (Supplier supplier : suppliers) {
                Map<String, Object> supplierData = createSupplierDataPoint(supplier, endDate);
                historicalData.add(supplierData);
            }
        }
        
        // Apply filters if specified
        if (request.getFilters() != null) {
            historicalData = applyFilters(historicalData, request.getFilters());
        }
        
        logger.debug("Collected {} historical data points", historicalData.size());
        return historicalData;
    }
    
    /**
     * Create a data point from supplier information
     */
    private Map<String, Object> createSupplierDataPoint(Supplier supplier, LocalDateTime timestamp) {
        Map<String, Object> dataPoint = new HashMap<>();
        
        dataPoint.put("supplier_id", supplier.getId());
        dataPoint.put("timestamp", timestamp);
        dataPoint.put("risk_score", supplier.getOverallRiskScore());
        dataPoint.put("financial_risk", supplier.getFinancialRiskScore());
        dataPoint.put("operational_risk", supplier.getOperationalRiskScore());
        dataPoint.put("compliance_risk", supplier.getComplianceRiskScore());
        dataPoint.put("geographic_risk", supplier.getGeographicRiskScore());
        dataPoint.put("delivery_rate", supplier.getOnTimeDeliveryRate());
        dataPoint.put("quality_rating", supplier.getQualityRating());
        dataPoint.put("cost_competitiveness", supplier.getCostCompetitivenessScore());
        dataPoint.put("responsiveness", supplier.getResponsivenessScore());
        dataPoint.put("country", supplier.getCountry());
        dataPoint.put("industry", supplier.getIndustry());
        dataPoint.put("tier", supplier.getTier().toString());
        dataPoint.put("annual_revenue", supplier.getAnnualRevenue());
        dataPoint.put("employee_count", supplier.getEmployeeCount());
        dataPoint.put("years_in_business", supplier.getYearsInBusiness());
        
        return dataPoint;
    }
    
    /**
     * Calculate enhanced risk scores combining traditional assessment with ML insights
     */
    private Map<String, BigDecimal> calculateEnhancedRiskScores(AnalyticsRequest request, 
                                                               MLPredictionResult predictions) {
        Map<String, BigDecimal> riskScores = new HashMap<>();
        
        // Extract risk predictions from ML results
        Map<String, Object> mlPredictions = predictions.getPredictions();
        
        if (mlPredictions.containsKey("risk_trend")) {
            Object riskTrend = mlPredictions.get("risk_trend");
            if (riskTrend instanceof Number) {
                riskScores.put("predicted_risk_trend", 
                    BigDecimal.valueOf(((Number) riskTrend).doubleValue()));
            }
        }
        
        // Add confidence-weighted risk scores
        BigDecimal confidence = predictions.getConfidence();
        riskScores.put("prediction_confidence", confidence);
        
        // Calculate composite risk score
        BigDecimal compositeRisk = calculateCompositeRiskScore(mlPredictions, confidence);
        riskScores.put("composite_risk", compositeRisk);
        
        return riskScores;
    }
    
    /**
     * Generate intelligent recommendations based on analytics results
     */
    private List<Map<String, Object>> generateIntelligentRecommendations(
            MLPredictionResult predictions, 
            Map<String, BigDecimal> riskScores, 
            Map<String, Object> businessContext) {
        
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        // Risk-based recommendations
        BigDecimal compositeRisk = riskScores.get("composite_risk");
        if (compositeRisk != null && compositeRisk.compareTo(BigDecimal.valueOf(70)) > 0) {
            Map<String, Object> highRiskRec = new HashMap<>();
            highRiskRec.put("type", "HIGH_RISK_ALERT");
            highRiskRec.put("priority", "HIGH");
            highRiskRec.put("message", "High composite risk detected - consider immediate risk mitigation");
            highRiskRec.put("actions", Arrays.asList(
                "Review supplier performance metrics",
                "Implement enhanced monitoring",
                "Consider alternative suppliers"
            ));
            recommendations.add(highRiskRec);
        }
        
        // Trend-based recommendations
        BigDecimal riskTrend = riskScores.get("predicted_risk_trend");
        if (riskTrend != null && riskTrend.compareTo(BigDecimal.valueOf(10)) > 0) {
            Map<String, Object> trendRec = new HashMap<>();
            trendRec.put("type", "INCREASING_RISK_TREND");
            trendRec.put("priority", "MEDIUM");
            trendRec.put("message", "Risk trend is increasing - proactive measures recommended");
            trendRec.put("actions", Arrays.asList(
                "Schedule supplier performance review",
                "Increase monitoring frequency",
                "Prepare contingency plans"
            ));
            recommendations.add(trendRec);
        }
        
        // Confidence-based recommendations
        BigDecimal confidence = riskScores.get("prediction_confidence");
        if (confidence != null && confidence.compareTo(BigDecimal.valueOf(60)) < 0) {
            Map<String, Object> confidenceRec = new HashMap<>();
            confidenceRec.put("type", "LOW_PREDICTION_CONFIDENCE");
            confidenceRec.put("priority", "LOW");
            confidenceRec.put("message", "Low prediction confidence - more data collection recommended");
            confidenceRec.put("actions", Arrays.asList(
                "Collect additional supplier data",
                "Increase reporting frequency",
                "Verify data quality"
            ));
            recommendations.add(confidenceRec);
        }
        
        return recommendations;
    }
    
    /**
     * Create and save analytics result
     */
    private AnalyticsResult createAnalyticsResult(AnalyticsRequest request,
                                                 MLPredictionResult predictions,
                                                 Map<String, BigDecimal> riskScores,
                                                 List<Map<String, Object>> recommendations,
                                                 long startTime) throws JsonProcessingException {
        
        AnalyticsResult result = new AnalyticsResult();
        result.setAnalysisType(request.getAnalysisType());
        result.setTimeHorizonDays(request.getTimeHorizonDays());
        result.setConfidenceScore(predictions.getConfidence());
        result.setModelVersion(predictions.getModelVersion());
        result.setProcessingTimeMs(System.currentTimeMillis() - startTime);
        
        // Convert objects to JSON strings for storage
        result.setPredictions(objectMapper.writeValueAsString(predictions.getPredictions()));
        result.setRiskScores(objectMapper.writeValueAsString(riskScores));
        result.setRecommendations(objectMapper.writeValueAsString(recommendations));
        
        // Set expiration time based on analysis type
        LocalDateTime expirationTime = LocalDateTime.now().plusDays(
            getExpirationDaysForAnalysisType(request.getAnalysisType())
        );
        result.setExpiresAt(expirationTime);
        
        return analyticsResultRepository.save(result);
    }
    
    // Helper methods for data processing and calculations
    
    private List<Map<String, Object>> applyFilters(List<Map<String, Object>> data, 
                                                  Map<String, Object> filters) {
        return data.stream()
            .filter(dataPoint -> {
                for (Map.Entry<String, Object> filter : filters.entrySet()) {
                    Object value = dataPoint.get(filter.getKey());
                    if (!Objects.equals(value, filter.getValue())) {
                        return false;
                    }
                }
                return true;
            })
            .collect(Collectors.toList());
    }
    
    private Map<String, Object> collectRealTimeSupplierData(Supplier supplier, 
                                                           Map<String, Object> context) {
        Map<String, Object> realTimeData = new HashMap<>();
        
        // Current supplier metrics
        realTimeData.put("current_risk_score", supplier.getOverallRiskScore());
        realTimeData.put("delivery_performance", supplier.getOnTimeDeliveryRate());
        realTimeData.put("quality_metrics", supplier.getQualityRating());
        realTimeData.put("financial_health", supplier.getCreditRating());
        
        // Add context data if provided
        if (context != null) {
            realTimeData.putAll(context);
        }
        
        // Add timestamp for real-time processing
        realTimeData.put("timestamp", LocalDateTime.now());
        
        return realTimeData;
    }
    
    private Map<String, Integer> calculateCurrentRiskScores(Supplier supplier) {
        Map<String, Integer> currentRisk = new HashMap<>();
        currentRisk.put("overall", supplier.getOverallRiskScore());
        currentRisk.put("financial", supplier.getFinancialRiskScore());
        currentRisk.put("operational", supplier.getOperationalRiskScore());
        currentRisk.put("compliance", supplier.getComplianceRiskScore());
        currentRisk.put("geographic", supplier.getGeographicRiskScore());
        return currentRisk;
    }
    
    private List<Map<String, Object>> generateRiskAlerts(Supplier supplier,
                                                        Map<String, Integer> currentRisk,
                                                        RiskPrediction futureRisk) {
        List<Map<String, Object>> alerts = new ArrayList<>();
        
        // Check for high current risk
        if (currentRisk.get("overall") > 80) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("type", "HIGH_CURRENT_RISK");
            alert.put("severity", "CRITICAL");
            alert.put("message", "Current overall risk exceeds critical threshold");
            alerts.add(alert);
        }
        
        // Check for increasing risk trend
        BigDecimal riskTrend = futureRisk.getOverallRiskTrend();
        if (riskTrend != null && riskTrend.compareTo(BigDecimal.valueOf(15)) > 0) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("type", "INCREASING_RISK_TREND");
            alert.put("severity", "HIGH");
            alert.put("message", "Significant risk increase predicted");
            alerts.add(alert);
        }
        
        return alerts;
    }
    
    private BigDecimal calculateCompositeRiskScore(Map<String, Object> mlPredictions, 
                                                  BigDecimal confidence) {
        // Simple composite calculation - can be enhanced with more sophisticated algorithms
        double baseScore = 50.0; // Default middle risk
        
        if (mlPredictions.containsKey("risk_trend")) {
            Object trend = mlPredictions.get("risk_trend");
            if (trend instanceof Number) {
                baseScore += ((Number) trend).doubleValue();
            }
        }
        
        // Apply confidence weighting
        double confidenceWeight = confidence.doubleValue() / 100.0;
        double compositeScore = baseScore * confidenceWeight + (50.0 * (1 - confidenceWeight));
        
        return BigDecimal.valueOf(Math.max(0, Math.min(100, compositeScore)));
    }
    
    private Map<String, Object> calculateRiskTrend(Map<String, Integer> currentRisk, 
                                                  RiskPrediction futureRisk) {
        Map<String, Object> trend = new HashMap<>();
        
        // Overall trend
        BigDecimal overallTrend = futureRisk.getOverallRiskTrend();
        trend.put("overall_trend", overallTrend);
        trend.put("trend_direction", overallTrend.compareTo(BigDecimal.ZERO) > 0 ? "INCREASING" : "DECREASING");
        
        // Risk category trends
        Map<String, BigDecimal> riskScores = futureRisk.getRiskScores();
        if (riskScores != null) {
            for (Map.Entry<String, BigDecimal> entry : riskScores.entrySet()) {
                if (entry.getKey().startsWith("predicted_")) {
                    String category = entry.getKey().replace("predicted_", "");
                    Integer currentValue = currentRisk.get(category);
                    if (currentValue != null) {
                        BigDecimal change = entry.getValue().subtract(BigDecimal.valueOf(currentValue));
                        trend.put(category + "_change", change);
                    }
                }
            }
        }
        
        return trend;
    }
    
    private void broadcastAnalyticsUpdate(AnalyticsResult result) {
        try {
            Map<String, Object> update = new HashMap<>();
            update.put("analyticsId", result.getId());
            update.put("analysisType", result.getAnalysisType());
            update.put("confidence", result.getConfidenceScore());
            update.put("processingTime", result.getProcessingTimeMs());
            update.put("timestamp", result.getCreatedAt());
            
            realTimeUpdateService.broadcastDashboardUpdate("analytics_completed", update);
        } catch (Exception e) {
            logger.warn("Failed to broadcast analytics update: {}", e.getMessage());
        }
    }
    
    // Performance calculation methods
    
    private Double calculateAverageProcessingTime(List<AnalyticsResult> results) {
        return results.stream()
            .filter(r -> r.getProcessingTimeMs() != null)
            .mapToLong(AnalyticsResult::getProcessingTimeMs)
            .average()
            .orElse(0.0);
    }
    
    private Double calculateAverageConfidence(List<AnalyticsResult> results) {
        return results.stream()
            .filter(r -> r.getConfidenceScore() != null)
            .mapToDouble(r -> r.getConfidenceScore().doubleValue())
            .average()
            .orElse(0.0);
    }
    
    private Map<String, Long> getAnalyticsBreakdownByType(List<AnalyticsResult> results) {
        return results.stream()
            .collect(Collectors.groupingBy(
                AnalyticsResult::getAnalysisType,
                Collectors.counting()
            ));
    }
    
    private Map<String, Object> calculatePerformanceTrends(List<AnalyticsResult> results) {
        Map<String, Object> trends = new HashMap<>();
        
        // Sort by creation time
        List<AnalyticsResult> sortedResults = results.stream()
            .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
            .collect(Collectors.toList());
        
        if (sortedResults.size() >= 2) {
            // Calculate processing time trend
            AnalyticsResult first = sortedResults.get(0);
            AnalyticsResult last = sortedResults.get(sortedResults.size() - 1);
            
            if (first.getProcessingTimeMs() != null && last.getProcessingTimeMs() != null) {
                double processingTimeTrend = last.getProcessingTimeMs() - first.getProcessingTimeMs();
                trends.put("processing_time_trend", processingTimeTrend);
            }
            
            if (first.getConfidenceScore() != null && last.getConfidenceScore() != null) {
                BigDecimal confidenceTrend = last.getConfidenceScore().subtract(first.getConfidenceScore());
                trends.put("confidence_trend", confidenceTrend);
            }
        }
        
        return trends;
    }
    
    private int getExpirationDaysForAnalysisType(String analysisType) {
        return switch (analysisType) {
            case "REAL_TIME_RISK" -> 1;
            case "SHORT_TERM_FORECAST" -> 7;
            case "LONG_TERM_FORECAST" -> 30;
            default -> 14;
        };
    }
    
    // Custom exception for analytics service errors
    public static class AnalyticsException extends RuntimeException {
        public AnalyticsException(String message) {
            super(message);
        }
        
        public AnalyticsException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
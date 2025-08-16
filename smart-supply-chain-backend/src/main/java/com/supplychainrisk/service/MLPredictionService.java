package com.supplychainrisk.service;

import com.supplychainrisk.dto.MLPredictionResult;
import com.supplychainrisk.dto.RiskPrediction;
import com.supplychainrisk.entity.Supplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MLPredictionService {
    
    private static final Logger logger = LoggerFactory.getLogger(MLPredictionService.class);
    
    @Value("${ml.service.url:http://localhost:5000}")
    private String mlServiceUrl;
    
    @Value("${ml.service.enabled:false}")
    private boolean mlServiceEnabled;
    
    @Value("${ml.fallback.enabled:true}")
    private boolean fallbackEnabled;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Predict future risk levels using ML models
     */
    public RiskPrediction predictRisk(Supplier supplier, Map<String, Object> realTimeData, Duration timeHorizon) {
        logger.info("Predicting risk for supplier {} with time horizon {} days", 
                   supplier.getId(), timeHorizon.toDays());
        
        try {
            if (mlServiceEnabled) {
                return callMLServiceForRiskPrediction(supplier, realTimeData, timeHorizon);
            } else {
                logger.info("ML service disabled, using fallback risk prediction");
                return generateFallbackRiskPrediction(supplier, timeHorizon);
            }
        } catch (Exception e) {
            logger.error("Error in ML risk prediction for supplier {}: {}", supplier.getId(), e.getMessage());
            if (fallbackEnabled) {
                logger.info("Using fallback risk prediction due to ML service error");
                return generateFallbackRiskPrediction(supplier, timeHorizon);
            } else {
                throw new MLServiceException("ML service failed and fallback is disabled", e);
            }
        }
    }
    
    /**
     * Generate ML predictions for various analytics
     */
    public MLPredictionResult predict(List<Map<String, Object>> historicalData, Duration predictionHorizon) {
        logger.info("Generating ML predictions for {} data points with horizon {} days", 
                   historicalData.size(), predictionHorizon.toDays());
        
        try {
            if (mlServiceEnabled) {
                return callMLServiceForPrediction(historicalData, predictionHorizon);
            } else {
                return generateFallbackPrediction(historicalData, predictionHorizon);
            }
        } catch (Exception e) {
            logger.error("Error in ML prediction: {}", e.getMessage());
            if (fallbackEnabled) {
                return generateFallbackPrediction(historicalData, predictionHorizon);
            } else {
                throw new MLServiceException("ML service failed and fallback is disabled", e);
            }
        }
    }
    
    /**
     * Call external ML service for risk prediction
     */
    private RiskPrediction callMLServiceForRiskPrediction(Supplier supplier, 
                                                         Map<String, Object> realTimeData, 
                                                         Duration timeHorizon) {
        Map<String, Object> request = new HashMap<>();
        request.put("supplierId", supplier.getId());
        request.put("supplierProfile", createSupplierProfile(supplier));
        request.put("realTimeData", realTimeData);
        request.put("timeHorizonDays", timeHorizon.toDays());
        request.put("modelType", "risk_prediction");
        
        ResponseEntity<Map> response = restTemplate.postForEntity(
            mlServiceUrl + "/predict-risk", request, Map.class);
        
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return mapToRiskPrediction(response.getBody(), supplier.getId(), (int) timeHorizon.toDays());
        } else {
            throw new MLServiceException("ML service returned invalid response");
        }
    }
    
    /**
     * Call external ML service for general predictions
     */
    private MLPredictionResult callMLServiceForPrediction(List<Map<String, Object>> historicalData, 
                                                         Duration predictionHorizon) {
        Map<String, Object> request = new HashMap<>();
        request.put("historicalData", historicalData);
        request.put("predictionHorizonDays", predictionHorizon.toDays());
        request.put("features", extractFeatures(historicalData));
        request.put("modelType", "time_series_forecast");
        
        ResponseEntity<Map> response = restTemplate.postForEntity(
            mlServiceUrl + "/predict", request, Map.class);
        
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return mapToMLPredictionResult(response.getBody());
        } else {
            throw new MLServiceException("ML service returned invalid response");
        }
    }
    
    /**
     * Generate fallback risk prediction using statistical methods
     */
    private RiskPrediction generateFallbackRiskPrediction(Supplier supplier, Duration timeHorizon) {
        logger.debug("Generating fallback risk prediction for supplier {}", supplier.getId());
        
        RiskPrediction prediction = new RiskPrediction();
        prediction.setSupplierId(supplier.getId());
        prediction.setTimeHorizonDays((int) timeHorizon.toDays());
        prediction.setPredictedAt(LocalDateTime.now());
        
        // Generate risk scores based on current supplier data with some statistical modeling
        Map<String, BigDecimal> riskScores = new HashMap<>();
        
        // Current risk scores
        riskScores.put("current_overall", BigDecimal.valueOf(supplier.getOverallRiskScore()));
        riskScores.put("current_financial", BigDecimal.valueOf(supplier.getFinancialRiskScore()));
        riskScores.put("current_operational", BigDecimal.valueOf(supplier.getOperationalRiskScore()));
        riskScores.put("current_compliance", BigDecimal.valueOf(supplier.getComplianceRiskScore()));
        riskScores.put("current_geographic", BigDecimal.valueOf(supplier.getGeographicRiskScore()));
        
        // Predicted risk scores (using simple trend analysis with random variation)
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double trendFactor = 1.0 + (random.nextDouble(-0.1, 0.1)); // ±10% variation
        
        riskScores.put("predicted_overall", 
            BigDecimal.valueOf(Math.max(0, Math.min(100, supplier.getOverallRiskScore() * trendFactor))));
        riskScores.put("predicted_financial", 
            BigDecimal.valueOf(Math.max(0, Math.min(100, supplier.getFinancialRiskScore() * trendFactor))));
        riskScores.put("predicted_operational", 
            BigDecimal.valueOf(Math.max(0, Math.min(100, supplier.getOperationalRiskScore() * trendFactor))));
        
        prediction.setRiskScores(riskScores);
        
        // Calculate overall risk trend
        BigDecimal currentOverall = BigDecimal.valueOf(supplier.getOverallRiskScore());
        BigDecimal predictedOverall = riskScores.get("predicted_overall");
        prediction.setOverallRiskTrend(predictedOverall.subtract(currentOverall));
        
        // Generate risk factors
        Map<String, Object> riskFactors = new HashMap<>();
        riskFactors.put("delivery_performance", supplier.getOnTimeDeliveryRate());
        riskFactors.put("quality_rating", supplier.getQualityRating());
        riskFactors.put("financial_stability", supplier.getCreditRating());
        riskFactors.put("geographic_risk", supplier.getCountry());
        riskFactors.put("certification_count", 
            Optional.ofNullable(supplier.getIsoCertifications()).orElse(Collections.emptyList()).size());
        
        prediction.setRiskFactors(riskFactors);
        
        // Generate risk alerts based on thresholds
        List<String> alerts = new ArrayList<>();
        if (predictedOverall.compareTo(BigDecimal.valueOf(70)) > 0) {
            alerts.add("High risk prediction - consider enhanced monitoring");
        }
        if (prediction.getOverallRiskTrend().compareTo(BigDecimal.valueOf(10)) > 0) {
            alerts.add("Risk trend is increasing - review supplier performance");
        }
        
        prediction.setRiskAlerts(alerts);
        
        // Confidence score based on data completeness
        BigDecimal confidence = calculateConfidenceScore(supplier);
        prediction.setConfidence(confidence);
        
        return prediction;
    }
    
    /**
     * Generate fallback prediction using statistical methods
     */
    private MLPredictionResult generateFallbackPrediction(List<Map<String, Object>> historicalData, 
                                                         Duration predictionHorizon) {
        logger.debug("Generating fallback prediction for {} data points", historicalData.size());
        
        MLPredictionResult.Builder resultBuilder = MLPredictionResult.builder()
            .modelVersion("fallback-v1.0")
            .generatedAt(LocalDateTime.now());
        
        // Simple statistical predictions
        Map<String, Object> predictions = new HashMap<>();
        
        if (!historicalData.isEmpty()) {
            // Calculate trend-based predictions
            predictions.put("risk_trend", calculateSimpleTrend(historicalData, "risk_score"));
            predictions.put("performance_trend", calculateSimpleTrend(historicalData, "performance_score"));
            predictions.put("delivery_prediction", calculateDeliveryPrediction(historicalData));
        }
        
        // Add prediction horizon info
        predictions.put("forecast_horizon_days", predictionHorizon.toDays());
        predictions.put("prediction_method", "statistical_fallback");
        
        resultBuilder.predictions(predictions);
        
        // Features extracted from historical data
        Map<String, Object> features = extractFeatures(historicalData);
        resultBuilder.features(features);
        
        // Confidence based on data quality
        BigDecimal confidence = calculateDataQualityConfidence(historicalData);
        resultBuilder.confidence(confidence);
        
        // Uncertainty bounds (wider for fallback method)
        Map<String, BigDecimal> uncertaintyBounds = new HashMap<>();
        uncertaintyBounds.put("lower_bound", confidence.multiply(BigDecimal.valueOf(0.7)));
        uncertaintyBounds.put("upper_bound", confidence.multiply(BigDecimal.valueOf(1.3)));
        resultBuilder.uncertaintyBounds(uncertaintyBounds);
        
        return resultBuilder.build();
    }
    
    /**
     * Create supplier profile for ML service
     */
    private Map<String, Object> createSupplierProfile(Supplier supplier) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", supplier.getId());
        profile.put("name", supplier.getName());
        profile.put("tier", supplier.getTier().toString());
        profile.put("country", supplier.getCountry());
        profile.put("industry", supplier.getIndustry());
        profile.put("annual_revenue", supplier.getAnnualRevenue());
        profile.put("employee_count", supplier.getEmployeeCount());
        profile.put("years_in_business", supplier.getYearsInBusiness());
        profile.put("overall_risk_score", supplier.getOverallRiskScore());
        profile.put("financial_risk_score", supplier.getFinancialRiskScore());
        profile.put("operational_risk_score", supplier.getOperationalRiskScore());
        profile.put("compliance_risk_score", supplier.getComplianceRiskScore());
        profile.put("geographic_risk_score", supplier.getGeographicRiskScore());
        profile.put("on_time_delivery_rate", supplier.getOnTimeDeliveryRate());
        profile.put("quality_rating", supplier.getQualityRating());
        profile.put("credit_rating", supplier.getCreditRating());
        profile.put("iso_certifications", supplier.getIsoCertifications());
        profile.put("compliance_certifications", supplier.getComplianceCertifications());
        
        return profile;
    }
    
    /**
     * Extract features from historical data
     */
    private Map<String, Object> extractFeatures(List<Map<String, Object>> historicalData) {
        Map<String, Object> features = new HashMap<>();
        
        if (historicalData.isEmpty()) {
            return features;
        }
        
        // Statistical features
        features.put("data_points", historicalData.size());
        features.put("time_span_days", calculateTimeSpan(historicalData));
        features.put("data_completeness", calculateDataCompleteness(historicalData));
        
        // Aggregate features
        features.put("avg_risk_score", calculateAverage(historicalData, "risk_score"));
        features.put("max_risk_score", calculateMaximum(historicalData, "risk_score"));
        features.put("min_risk_score", calculateMinimum(historicalData, "risk_score"));
        features.put("risk_volatility", calculateVolatility(historicalData, "risk_score"));
        
        return features;
    }
    
    // Helper methods for statistical calculations
    private BigDecimal calculateSimpleTrend(List<Map<String, Object>> data, String field) {
        if (data.size() < 2) return BigDecimal.ZERO;
        
        double sum = 0;
        int count = 0;
        
        for (int i = 1; i < data.size(); i++) {
            Object current = data.get(i).get(field);
            Object previous = data.get(i - 1).get(field);
            
            if (current instanceof Number && previous instanceof Number) {
                double change = ((Number) current).doubleValue() - ((Number) previous).doubleValue();
                sum += change;
                count++;
            }
        }
        
        return count > 0 ? BigDecimal.valueOf(sum / count) : BigDecimal.ZERO;
    }
    
    private Map<String, Object> calculateDeliveryPrediction(List<Map<String, Object>> data) {
        Map<String, Object> prediction = new HashMap<>();
        
        // Simple delivery performance prediction
        double avgDeliveryRate = calculateAverage(data, "delivery_rate").doubleValue();
        prediction.put("predicted_delivery_rate", avgDeliveryRate);
        prediction.put("confidence", avgDeliveryRate > 90 ? "HIGH" : avgDeliveryRate > 70 ? "MEDIUM" : "LOW");
        
        return prediction;
    }
    
    private BigDecimal calculateConfidenceScore(Supplier supplier) {
        double score = 50.0; // Base confidence
        
        // Increase confidence based on data completeness
        if (supplier.getAnnualRevenue() != null) score += 10;
        if (supplier.getEmployeeCount() != null) score += 10;
        if (supplier.getQualityRating() != null) score += 10;
        if (supplier.getOnTimeDeliveryRate() != null) score += 10;
        if (supplier.getCreditRating() != null) score += 10;
        
        return BigDecimal.valueOf(Math.min(100, score));
    }
    
    private BigDecimal calculateDataQualityConfidence(List<Map<String, Object>> data) {
        if (data.isEmpty()) return BigDecimal.valueOf(20);
        
        double score = 30.0; // Base score
        
        // More data points = higher confidence
        score += Math.min(40, data.size() * 2);
        
        // Data completeness
        score += calculateDataCompleteness(data).doubleValue() * 0.3;
        
        return BigDecimal.valueOf(Math.min(100, score));
    }
    
    private BigDecimal calculateAverage(List<Map<String, Object>> data, String field) {
        return data.stream()
            .map(d -> d.get(field))
            .filter(Objects::nonNull)
            .filter(v -> v instanceof Number)
            .map(v -> ((Number) v).doubleValue())
            .collect(java.util.stream.Collectors.averagingDouble(Double::doubleValue))
            .doubleValue() == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(
                data.stream()
                    .map(d -> d.get(field))
                    .filter(Objects::nonNull)
                    .filter(v -> v instanceof Number)
                    .map(v -> ((Number) v).doubleValue())
                    .collect(java.util.stream.Collectors.averagingDouble(Double::doubleValue))
            );
    }
    
    private BigDecimal calculateMaximum(List<Map<String, Object>> data, String field) {
        return BigDecimal.valueOf(
            data.stream()
                .map(d -> d.get(field))
                .filter(Objects::nonNull)
                .filter(v -> v instanceof Number)
                .map(v -> ((Number) v).doubleValue())
                .max(Double::compareTo)
                .orElse(0.0)
        );
    }
    
    private BigDecimal calculateMinimum(List<Map<String, Object>> data, String field) {
        return BigDecimal.valueOf(
            data.stream()
                .map(d -> d.get(field))
                .filter(Objects::nonNull)
                .filter(v -> v instanceof Number)
                .map(v -> ((Number) v).doubleValue())
                .min(Double::compareTo)
                .orElse(0.0)
        );
    }
    
    private BigDecimal calculateVolatility(List<Map<String, Object>> data, String field) {
        if (data.size() < 2) return BigDecimal.ZERO;
        
        BigDecimal avg = calculateAverage(data, field);
        double sumSquaredDiffs = data.stream()
            .map(d -> d.get(field))
            .filter(Objects::nonNull)
            .filter(v -> v instanceof Number)
            .map(v -> ((Number) v).doubleValue())
            .map(v -> Math.pow(v - avg.doubleValue(), 2))
            .reduce(0.0, Double::sum);
        
        return BigDecimal.valueOf(Math.sqrt(sumSquaredDiffs / data.size()));
    }
    
    private BigDecimal calculateTimeSpan(List<Map<String, Object>> data) {
        // Simplified - assume data covers reasonable time span
        return BigDecimal.valueOf(data.size() * 7); // Weekly data points
    }
    
    private BigDecimal calculateDataCompleteness(List<Map<String, Object>> data) {
        if (data.isEmpty()) return BigDecimal.ZERO;
        
        int totalFields = data.size() * 5; // Assume 5 key fields per record
        int completeFields = 0;
        
        for (Map<String, Object> record : data) {
            completeFields += record.values().stream()
                .map(v -> v != null ? 1 : 0)
                .reduce(0, Integer::sum);
        }
        
        return BigDecimal.valueOf((double) completeFields / totalFields * 100);
    }
    
    // Mapping methods for ML service responses
    private RiskPrediction mapToRiskPrediction(Map<String, Object> response, Long supplierId, int timeHorizonDays) {
        RiskPrediction prediction = new RiskPrediction();
        prediction.setSupplierId(supplierId);
        prediction.setTimeHorizonDays(timeHorizonDays);
        prediction.setPredictedAt(LocalDateTime.now());
        
        // Map response fields to RiskPrediction
        if (response.containsKey("risk_scores")) {
            prediction.setRiskScores((Map<String, BigDecimal>) response.get("risk_scores"));
        }
        if (response.containsKey("risk_factors")) {
            prediction.setRiskFactors((Map<String, Object>) response.get("risk_factors"));
        }
        if (response.containsKey("risk_trend")) {
            prediction.setOverallRiskTrend(BigDecimal.valueOf(((Number) response.get("risk_trend")).doubleValue()));
        }
        if (response.containsKey("alerts")) {
            prediction.setRiskAlerts((List<String>) response.get("alerts"));
        }
        if (response.containsKey("confidence")) {
            prediction.setConfidence(BigDecimal.valueOf(((Number) response.get("confidence")).doubleValue()));
        }
        
        return prediction;
    }
    
    private MLPredictionResult mapToMLPredictionResult(Map<String, Object> response) {
        return MLPredictionResult.builder()
            .predictions((Map<String, Object>) response.get("predictions"))
            .confidence(BigDecimal.valueOf(((Number) response.getOrDefault("confidence", 75)).doubleValue()))
            .modelVersion((String) response.getOrDefault("model_version", "unknown"))
            .generatedAt(LocalDateTime.now())
            .features((Map<String, Object>) response.get("features"))
            .uncertaintyBounds((Map<String, BigDecimal>) response.get("uncertainty_bounds"))
            .build();
    }
    
    // Custom exception for ML service errors
    /**
     * Generate real-time ML prediction using current data
     */
    public MLPredictionResult generateRealTimePrediction(Object dataSet) {
        logger.info("Generating real-time ML prediction");
        
        try {
            // Convert data set to ML input format
            Map<String, Object> inputData = convertToMLInput(dataSet);
            
            if (mlServiceEnabled) {
                return callMLServiceForRealTimePrediction(inputData);
            } else {
                return generateFallbackRealTimePrediction(inputData);
            }
        } catch (Exception e) {
            logger.error("Real-time ML prediction failed: {}", e.getMessage());
            if (fallbackEnabled) {
                return generateFallbackRealTimePrediction(convertToMLInput(dataSet));
            } else {
                throw new MLServiceException("Real-time ML prediction failed", e);
            }
        }
    }
    
    /**
     * Get current model version for tracking
     */
    public String getCurrentModelVersion() {
        return "v1.0.0"; // This could be dynamic from config or ML service
    }
    
    private Map<String, Object> convertToMLInput(Object dataSet) {
        Map<String, Object> input = new HashMap<>();
        
        if (dataSet != null) {
            input.put("data_type", dataSet.getClass().getSimpleName());
            input.put("timestamp", LocalDateTime.now().toString());
            
            // Add data set content if it has accessible fields
            try {
                if (dataSet instanceof Map) {
                    input.putAll((Map<String, Object>) dataSet);
                } else {
                    // Use reflection to extract relevant fields (simplified approach)
                    input.put("entity_data", dataSet.toString());
                }
            } catch (Exception e) {
                logger.debug("Could not extract detailed data from dataset: {}", e.getMessage());
            }
        }
        
        return input;
    }
    
    private MLPredictionResult callMLServiceForRealTimePrediction(Map<String, Object> inputData) {
        Map<String, Object> request = new HashMap<>();
        request.put("input_data", inputData);
        request.put("prediction_type", "real_time_risk");
        request.put("model_type", "ensemble");
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                mlServiceUrl + "/predict/realtime", request, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return mapToMLPredictionResult(response.getBody());
            } else {
                throw new MLServiceException("ML service returned invalid response for real-time prediction");
            }
        } catch (Exception e) {
            logger.error("ML service call failed for real-time prediction", e);
            throw new MLServiceException("ML service call failed", e);
        }
    }
    
    private MLPredictionResult generateFallbackRealTimePrediction(Map<String, Object> inputData) {
        logger.debug("Generating fallback real-time prediction");
        
        // Generate basic predictions using statistical methods
        Map<String, Object> predictions = new HashMap<>();
        predictions.put("risk_score", 45.0 + (Math.random() * 20)); // Random between 45-65
        predictions.put("trend", "stable");
        predictions.put("prediction_method", "fallback_realtime");
        predictions.put("data_quality", calculateDataQuality(inputData));
        
        return MLPredictionResult.builder()
            .predictions(predictions)
            .confidence(BigDecimal.valueOf(65.0)) // Lower confidence for fallback
            .modelVersion(getCurrentModelVersion())
            .generatedAt(LocalDateTime.now())
            .features(inputData)
            .build();
    }
    
    private Double calculateDataQuality(Map<String, Object> inputData) {
        if (inputData == null || inputData.isEmpty()) {
            return 30.0; // Poor quality
        }
        
        int nonNullFields = 0;
        int totalFields = inputData.size();
        
        for (Object value : inputData.values()) {
            if (value != null && !value.toString().trim().isEmpty()) {
                nonNullFields++;
            }
        }
        
        return (double) nonNullFields / totalFields * 100.0;
    }
    
    public static class MLServiceException extends RuntimeException {
        public MLServiceException(String message) {
            super(message);
        }
        
        public MLServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
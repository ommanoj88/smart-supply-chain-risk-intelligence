package com.supplychainrisk.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplychainrisk.dto.MLPredictionRequest;
import com.supplychainrisk.dto.MLPredictionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

/**
 * Service for integrating with ML prediction microservice
 * Provides delay prediction, anomaly detection, and risk scoring capabilities
 */
@Service
public class MLPredictionService {

    private static final Logger logger = LoggerFactory.getLogger(MLPredictionService.class);

    @Value("${ml.service.url:http://localhost:5000}")
    private String mlServiceUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public MLPredictionService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Predict shipment delay using ML service
     */
    @Cacheable(value = "ml-predictions", key = "#request.shipmentId + '_' + #request.carrier + '_' + #request.distanceKm")
    public MLPredictionResponse predictShipmentDelay(MLPredictionRequest request) {
        try {
            String url = mlServiceUrl + "/predict/delay";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<MLPredictionRequest> httpEntity = new HttpEntity<>(request, headers);
            
            logger.info("Calling ML service for delay prediction: {}", url);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, httpEntity, String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                return MLPredictionResponse.builder()
                    .predictedDelayHours(jsonResponse.get("predicted_delay_hours").asDouble())
                    .confidenceScore(jsonResponse.get("confidence_score").asDouble())
                    .riskLevel(jsonResponse.get("risk_level").asText())
                    .recommendations(parseRecommendations(jsonResponse.get("recommendations")))
                    .success(true)
                    .build();
            } else {
                logger.warn("ML service returned non-200 status: {}", response.getStatusCode());
                return createFallbackPrediction(request);
            }
            
        } catch (HttpClientErrorException e) {
            logger.error("HTTP error calling ML service: {}", e.getMessage());
            return createFallbackPrediction(request);
        } catch (ResourceAccessException e) {
            logger.error("ML service unavailable: {}", e.getMessage());
            return createFallbackPrediction(request);
        } catch (Exception e) {
            logger.error("Unexpected error calling ML service: {}", e.getMessage(), e);
            return createFallbackPrediction(request);
        }
    }

    /**
     * Calculate comprehensive risk score using ML service
     */
    @Cacheable(value = "risk-scores", key = "#riskData.hashCode()")
    public Map<String, Object> calculateRiskScore(Map<String, Object> riskData) {
        try {
            String url = mlServiceUrl + "/predict/risk-score";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(riskData, headers);
            
            logger.info("Calling ML service for risk score calculation: {}", url);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.POST, httpEntity, Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                logger.warn("ML service returned non-200 status for risk score: {}", response.getStatusCode());
                return createFallbackRiskScore(riskData);
            }
            
        } catch (Exception e) {
            logger.error("Error calculating risk score via ML service: {}", e.getMessage(), e);
            return createFallbackRiskScore(riskData);
        }
    }

    /**
     * Detect supplier anomalies using ML service
     */
    public Map<String, Object> detectSupplierAnomalies(List<Map<String, Object>> supplierData) {
        try {
            String url = mlServiceUrl + "/analyze/anomalies";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<List<Map<String, Object>>> httpEntity = new HttpEntity<>(supplierData, headers);
            
            logger.info("Calling ML service for anomaly detection: {}", url);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.POST, httpEntity, Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                logger.warn("ML service returned non-200 status for anomaly detection: {}", response.getStatusCode());
                return createFallbackAnomalyResult(supplierData);
            }
            
        } catch (Exception e) {
            logger.error("Error detecting anomalies via ML service: {}", e.getMessage(), e);
            return createFallbackAnomalyResult(supplierData);
        }
    }

    /**
     * Check if ML service is healthy
     */
    public boolean isMLServiceHealthy() {
        try {
            String url = mlServiceUrl + "/health";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.debug("ML service health check failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Train the delay prediction model with historical data
     */
    public Map<String, Object> trainDelayModel(List<Map<String, Object>> trainingData) {
        try {
            String url = mlServiceUrl + "/train/delay-model";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<List<Map<String, Object>>> httpEntity = new HttpEntity<>(trainingData, headers);
            
            logger.info("Training ML delay model with {} samples", trainingData.size());
            
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.POST, httpEntity, Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("ML model training completed successfully");
                return response.getBody();
            } else {
                logger.warn("ML service returned non-200 status for model training: {}", response.getStatusCode());
                return Map.of("success", false, "error", "Training failed");
            }
            
        } catch (Exception e) {
            logger.error("Error training ML model: {}", e.getMessage(), e);
            return Map.of("success", false, "error", e.getMessage());
        }
    }

    /**
     * Create fallback prediction when ML service is unavailable
     */
    private MLPredictionResponse createFallbackPrediction(MLPredictionRequest request) {
        logger.info("Using fallback prediction logic");
        
        // Simple heuristic-based prediction
        double baseDelay = 2.0; // Base 2 hours
        
        // Add delay based on distance
        Double distance = request.getDistanceKm();
        if (distance != null) {
            baseDelay += distance * 0.005; // 0.005 hours per km
        }
        
        // Add delay based on carrier
        String carrier = request.getCarrier();
        if (carrier != null) {
            switch (carrier.toUpperCase()) {
                case "DHL":
                    baseDelay += 1.0;
                    break;
                case "FEDEX":
                    baseDelay += 0.5;
                    break;
                case "UPS":
                    baseDelay += 0.8;
                    break;
                case "MAERSK":
                    baseDelay += 24.0; // Ocean freight
                    break;
                case "MSC":
                    baseDelay += 36.0; // Ocean freight
                    break;
            }
        }
        
        // Determine risk level
        String riskLevel;
        if (baseDelay < 4) {
            riskLevel = "LOW";
        } else if (baseDelay < 12) {
            riskLevel = "MEDIUM";
        } else if (baseDelay < 24) {
            riskLevel = "HIGH";
        } else {
            riskLevel = "CRITICAL";
        }
        
        // Generate recommendations
        List<String> recommendations = new ArrayList<>();
        if (baseDelay > 8) {
            recommendations.add("Consider expedited shipping option");
            recommendations.add("Notify customer about potential delay");
        }
        if (baseDelay > 24) {
            recommendations.add("Investigate alternative carriers");
            recommendations.add("Implement contingency plan");
        }
        if (recommendations.isEmpty()) {
            recommendations.add("Shipment on track - monitor regularly");
        }
        
        return MLPredictionResponse.builder()
            .predictedDelayHours(baseDelay)
            .confidenceScore(0.6) // Lower confidence for fallback
            .riskLevel(riskLevel)
            .recommendations(recommendations)
            .success(true)
            .fallback(true)
            .build();
    }

    /**
     * Create fallback risk score when ML service is unavailable
     */
    private Map<String, Object> createFallbackRiskScore(Map<String, Object> riskData) {
        logger.info("Using fallback risk score calculation");
        
        // Simple risk score calculation
        double riskScore = 50.0; // Base score
        
        // Adjust based on available data
        Object supplierRisk = riskData.get("supplier_risk_score");
        if (supplierRisk instanceof Number) {
            riskScore = ((Number) supplierRisk).doubleValue();
        }
        
        String riskLevel;
        if (riskScore < 30) {
            riskLevel = "LOW";
        } else if (riskScore < 60) {
            riskLevel = "MEDIUM";
        } else if (riskScore < 80) {
            riskLevel = "HIGH";
        } else {
            riskLevel = "CRITICAL";
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("total_risk_score", riskScore);
        result.put("risk_level", riskLevel);
        result.put("fallback", true);
        result.put("recommendations", List.of("Monitor risk factors closely"));
        
        return result;
    }

    /**
     * Create fallback anomaly result when ML service is unavailable
     */
    private Map<String, Object> createFallbackAnomalyResult(List<Map<String, Object>> supplierData) {
        logger.info("Using fallback anomaly detection");
        
        Map<String, Object> result = new HashMap<>();
        result.put("anomalies", new ArrayList<>());
        result.put("total_suppliers", supplierData.size());
        result.put("anomaly_rate", 0.0);
        result.put("fallback", true);
        
        return result;
    }

    /**
     * Parse recommendations from JSON response
     */
    private List<String> parseRecommendations(JsonNode recommendationsNode) {
        List<String> recommendations = new ArrayList<>();
        if (recommendationsNode != null && recommendationsNode.isArray()) {
            for (JsonNode node : recommendationsNode) {
                recommendations.add(node.asText());
            }
        }
        return recommendations;
    }
}
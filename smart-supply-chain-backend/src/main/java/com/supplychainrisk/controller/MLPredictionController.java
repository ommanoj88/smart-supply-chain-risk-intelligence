package com.supplychainrisk.controller;

import com.supplychainrisk.dto.MLPredictionRequest;
import com.supplychainrisk.dto.MLPredictionResponse;
import com.supplychainrisk.service.MLPredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * REST Controller for ML-powered predictions and analytics
 */
@RestController
@RequestMapping("/api/ml")
@Tag(name = "ML Predictions", description = "Machine Learning powered predictions and analytics")
@CrossOrigin(origins = "${cors.allowed.origins}")
public class MLPredictionController {

    private static final Logger logger = LoggerFactory.getLogger(MLPredictionController.class);

    @Autowired
    private MLPredictionService mlPredictionService;

    @PostMapping("/predict/delay")
    @Operation(summary = "Predict shipment delay using ML models")
    @ApiResponse(responseCode = "200", description = "Delay prediction generated successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<MLPredictionResponse> predictShipmentDelay(@RequestBody MLPredictionRequest request) {
        logger.info("Predicting delay for shipment: {}", request.getShipmentId());
        
        try {
            MLPredictionResponse prediction = mlPredictionService.predictShipmentDelay(request);
            return ResponseEntity.ok(prediction);
        } catch (Exception e) {
            logger.error("Error predicting shipment delay: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(MLPredictionResponse.builder()
                    .success(false)
                    .error("Failed to predict delay: " + e.getMessage())
                    .build());
        }
    }

    @PostMapping("/analyze/risk-score")
    @Operation(summary = "Calculate comprehensive risk score using ML models")
    @ApiResponse(responseCode = "200", description = "Risk score calculated successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<Map<String, Object>> calculateRiskScore(@RequestBody Map<String, Object> riskData) {
        logger.info("Calculating risk score for data: {}", riskData.keySet());
        
        try {
            Map<String, Object> riskScore = mlPredictionService.calculateRiskScore(riskData);
            return ResponseEntity.ok(riskScore);
        } catch (Exception e) {
            logger.error("Error calculating risk score: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to calculate risk score: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/analyze/anomalies")
    @Operation(summary = "Detect supplier performance anomalies using ML models")
    @ApiResponse(responseCode = "200", description = "Anomaly analysis completed successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<Map<String, Object>> detectSupplierAnomalies(@RequestBody List<Map<String, Object>> supplierData) {
        logger.info("Analyzing anomalies for {} suppliers", supplierData.size());
        
        try {
            Map<String, Object> anomalies = mlPredictionService.detectSupplierAnomalies(supplierData);
            return ResponseEntity.ok(anomalies);
        } catch (Exception e) {
            logger.error("Error detecting supplier anomalies: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to detect anomalies: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/train/delay-model")
    @Operation(summary = "Train the delay prediction model with historical data")
    @ApiResponse(responseCode = "200", description = "Model training initiated successfully")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> trainDelayModel(@RequestBody List<Map<String, Object>> trainingData) {
        logger.info("Training delay model with {} samples", trainingData.size());
        
        try {
            Map<String, Object> result = mlPredictionService.trainDelayModel(trainingData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error training delay model: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to train model: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Check ML service health status")
    @ApiResponse(responseCode = "200", description = "Health status retrieved successfully")
    public ResponseEntity<Map<String, Object>> getMLServiceHealth() {
        try {
            boolean isHealthy = mlPredictionService.isMLServiceHealthy();
            Map<String, Object> health = new HashMap<>();
            health.put("ml_service_healthy", isHealthy);
            health.put("status", isHealthy ? "UP" : "DOWN");
            health.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            logger.error("Error checking ML service health: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("ml_service_healthy", false);
            errorResponse.put("status", "DOWN");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/features")
    @Operation(summary = "Get available ML features and capabilities")
    @ApiResponse(responseCode = "200", description = "ML features retrieved successfully")
    public ResponseEntity<Map<String, Object>> getMLFeatures() {
        Map<String, Object> features = new HashMap<>();
        features.put("delay_prediction", true);
        features.put("anomaly_detection", true);
        features.put("risk_scoring", true);
        features.put("model_training", true);
        features.put("fallback_enabled", true);
        features.put("supported_carriers", List.of("DHL", "FedEx", "UPS", "Maersk", "MSC"));
        features.put("risk_levels", List.of("LOW", "MEDIUM", "HIGH", "CRITICAL"));
        
        return ResponseEntity.ok(features);
    }
}
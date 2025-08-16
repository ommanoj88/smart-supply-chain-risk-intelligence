package com.supplychainrisk.controller;

import com.supplychainrisk.dto.AnalyticsRequest;
import com.supplychainrisk.dto.SupplierRecommendation;
import com.supplychainrisk.dto.RecommendationCriteria;
import com.supplychainrisk.entity.AnalyticsResult;
import com.supplychainrisk.service.AnalyticsService;
import com.supplychainrisk.service.RecommendationEngine;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Advanced Analytics", description = "AI-powered analytics and predictive insights")
public class AnalyticsController {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @Autowired
    private RecommendationEngine recommendationEngine;
    
    /**
     * Generate comprehensive predictive analytics
     */
    @PostMapping("/predictive")
    @Operation(summary = "Generate predictive analytics", 
               description = "Generate AI-powered predictive analytics with ML models and risk assessment")
    public ResponseEntity<AnalyticsResult> generatePredictiveAnalytics(
            @RequestBody AnalyticsRequest request) {
        
        logger.info("Received request for predictive analytics: {}", request.getAnalysisType());
        
        try {
            AnalyticsResult result = analyticsService.generatePredictiveAnalytics(request);
            return ResponseEntity.ok(result);
        } catch (AnalyticsService.AnalyticsException e) {
            logger.error("Analytics generation failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            logger.error("Unexpected error in predictive analytics: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get real-time risk assessment for a supplier
     */
    @GetMapping("/risk-assessment/{supplierId}")
    @Operation(summary = "Real-time risk assessment", 
               description = "Get AI-enhanced real-time risk assessment for a specific supplier")
    public ResponseEntity<Map<String, Object>> getRealTimeRiskAssessment(
            @Parameter(description = "Supplier ID") @PathVariable Long supplierId,
            @RequestParam(required = false) Map<String, String> context) {
        
        logger.info("Real-time risk assessment requested for supplier: {}", supplierId);
        
        try {
            // Convert String context to Object context for processing
            Map<String, Object> objectContext = new HashMap<>();
            if (context != null) {
                objectContext.putAll(context);
            }
            
            Map<String, Object> assessment = analyticsService.assessRealTimeRisk(supplierId, objectContext);
            return ResponseEntity.ok(assessment);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid supplier ID: {}", supplierId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error in real-time risk assessment: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get analytics performance metrics
     */
    @GetMapping("/performance")
    @Operation(summary = "Analytics performance metrics", 
               description = "Get performance metrics and insights for analytics engine")
    public ResponseEntity<Map<String, Object>> getAnalyticsPerformance() {
        
        logger.debug("Analytics performance metrics requested");
        
        try {
            Map<String, Object> metrics = analyticsService.getAnalyticsPerformanceMetrics();
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            logger.error("Error retrieving analytics performance: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get supplier recommendations
     */
    @PostMapping("/recommendations/suppliers")
    @Operation(summary = "Intelligent supplier recommendations", 
               description = "Get AI-powered supplier recommendations based on criteria and risk analysis")
    public ResponseEntity<List<SupplierRecommendation>> getSupplierRecommendations(
            @Parameter(description = "Current supplier ID (optional)") @RequestParam(required = false) Long currentSupplierId,
            @RequestBody RecommendationCriteria criteria) {
        
        logger.info("Supplier recommendations requested for current supplier: {}", currentSupplierId);
        
        try {
            List<SupplierRecommendation> recommendations = recommendationEngine
                .recommendAlternativeSuppliers(currentSupplierId, criteria);
            return ResponseEntity.ok(recommendations);
        } catch (RecommendationEngine.RecommendationException e) {
            logger.error("Recommendation generation failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            logger.error("Unexpected error in supplier recommendations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get route recommendations
     */
    @GetMapping("/recommendations/routes")
    @Operation(summary = "Optimal route recommendations", 
               description = "Get intelligent route recommendations with cost, speed, and risk optimization")
    public ResponseEntity<List<Map<String, Object>>> getRouteRecommendations(
            @Parameter(description = "Origin location") @RequestParam String origin,
            @Parameter(description = "Destination location") @RequestParam String destination,
            @RequestParam(required = false) Map<String, String> criteria) {
        
        logger.info("Route recommendations requested from {} to {}", origin, destination);
        
        try {
            // Convert String criteria to Object criteria
            Map<String, Object> objectCriteria = new HashMap<>();
            if (criteria != null) {
                objectCriteria.putAll(criteria);
            }
            
            List<Map<String, Object>> recommendations = recommendationEngine
                .recommendOptimalRoutes(origin, destination, objectCriteria);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            logger.error("Error generating route recommendations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get inventory optimization recommendations
     */
    @PostMapping("/recommendations/inventory")
    @Operation(summary = "Inventory optimization recommendations", 
               description = "Get AI-powered inventory optimization recommendations based on supplier risk profiles")
    public ResponseEntity<Map<String, Object>> getInventoryRecommendations(
            @RequestBody Map<String, Object> request) {
        
        logger.info("Inventory optimization recommendations requested");
        
        try {
            @SuppressWarnings("unchecked")
            List<Long> supplierIds = (List<Long>) request.get("supplierIds");
            Map<String, Object> criteria = (Map<String, Object>) request.getOrDefault("criteria", new HashMap<>());
            
            if (supplierIds == null || supplierIds.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            Map<String, Object> recommendations = recommendationEngine
                .recommendInventoryOptimization(supplierIds, criteria);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            logger.error("Error generating inventory recommendations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get historical analytics results with pagination
     */
    @GetMapping("/history")
    @Operation(summary = "Historical analytics results", 
               description = "Get paginated historical analytics results with filtering")
    public ResponseEntity<Page<AnalyticsResult>> getHistoricalAnalytics(
            @Parameter(description = "Analysis type filter") @RequestParam(required = false) String analysisType,
            @Parameter(description = "Start date filter") @RequestParam(required = false) String startDate,
            @Parameter(description = "End date filter") @RequestParam(required = false) String endDate,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        logger.debug("Historical analytics requested - page: {}, size: {}", page, size);
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            
            // For this simplified implementation, we'll return empty page
            // In a full implementation, this would query the AnalyticsResultRepository with filters
            Page<AnalyticsResult> results = Page.empty(pageable);
            
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error retrieving historical analytics: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get analytics insights summary
     */
    @GetMapping("/insights")
    @Operation(summary = "Analytics insights summary", 
               description = "Get high-level analytics insights and trends")
    public ResponseEntity<Map<String, Object>> getAnalyticsInsights() {
        
        logger.debug("Analytics insights summary requested");
        
        try {
            Map<String, Object> insights = new HashMap<>();
            
            // Get performance metrics as base for insights
            Map<String, Object> performance = analyticsService.getAnalyticsPerformanceMetrics();
            
            // Generate insights summary
            insights.put("performance", performance);
            insights.put("timestamp", LocalDateTime.now());
            insights.put("status", "operational");
            
            // Add trend analysis
            Map<String, Object> trends = new HashMap<>();
            trends.put("accuracy_trend", "stable");
            trends.put("processing_speed", "improving");
            trends.put("confidence_level", "high");
            insights.put("trends", trends);
            
            // Add recommendations
            Map<String, Object> systemRecommendations = new HashMap<>();
            systemRecommendations.put("model_optimization", "Consider model retraining for improved accuracy");
            systemRecommendations.put("data_quality", "Maintain current data collection standards");
            insights.put("system_recommendations", systemRecommendations);
            
            return ResponseEntity.ok(insights);
        } catch (Exception e) {
            logger.error("Error generating analytics insights: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Trigger analytics model retraining (simulation)
     */
    @PostMapping("/model/retrain")
    @Operation(summary = "Trigger model retraining", 
               description = "Trigger retraining of ML models with latest data")
    public ResponseEntity<Map<String, Object>> triggerModelRetraining(
            @RequestBody(required = false) Map<String, Object> retrainParams) {
        
        logger.info("Model retraining triggered");
        
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "initiated");
            response.put("jobId", "retrain-" + System.currentTimeMillis());
            response.put("estimatedDuration", "30-60 minutes");
            response.put("message", "Model retraining job has been queued");
            response.put("timestamp", LocalDateTime.now());
            
            // In a real implementation, this would trigger actual ML model retraining
            logger.info("Model retraining simulation completed");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error triggering model retraining: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get ML model status and metrics
     */
    @GetMapping("/model/status")
    @Operation(summary = "ML model status", 
               description = "Get current status and performance metrics of ML models")
    public ResponseEntity<Map<String, Object>> getModelStatus() {
        
        logger.debug("ML model status requested");
        
        try {
            Map<String, Object> status = new HashMap<>();
            
            // Simulate model status information
            status.put("model_version", "v2.1.0");
            status.put("status", "active");
            status.put("last_training", LocalDateTime.now().minusDays(7));
            status.put("accuracy", 87.5);
            status.put("precision", 85.2);
            status.put("recall", 89.1);
            status.put("f1_score", 87.1);
            status.put("predictions_today", 245);
            status.put("avg_prediction_time_ms", 150);
            
            // Model health indicators
            Map<String, Object> health = new HashMap<>();
            health.put("data_quality", "good");
            health.put("drift_detected", false);
            health.put("performance_stable", true);
            health.put("next_retrain_due", LocalDateTime.now().plusDays(23));
            status.put("health", health);
            
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            logger.error("Error retrieving model status: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
package com.supplychainrisk.controller;

import com.supplychainrisk.dto.*;
import com.supplychainrisk.entity.AnalyticsResult;
import com.supplychainrisk.service.AnalyticsService;
import com.supplychainrisk.service.RecommendationEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:3000")
public class AnalyticsController {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @Autowired
    private RecommendationEngine recommendationEngine;
    
    /**
     * Generate predictive analytics with ML predictions and recommendations
     */
    @PostMapping("/predictive-analytics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<AnalyticsResult> generatePredictiveAnalytics(
            @RequestBody AnalyticsRequest request) {
        try {
            logger.info("Generating predictive analytics for request type: {}", request.getAnalysisType());
            
            AnalyticsResult result = analyticsService.generatePredictiveAnalytics(request);
            
            logger.info("Successfully generated predictive analytics with recommendations", 
                result.getRecommendations() != null ? result.getRecommendations().length() : 0);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Failed to generate predictive analytics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }
    
    /**
     * Get real-time risk assessment for a specific supplier
     */
    @GetMapping("/real-time-risk/{supplierId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<Map<String, Object>> getRealTimeRisk(
            @PathVariable Long supplierId,
            @RequestParam(required = false) Map<String, Object> context) {
        try {
            logger.info("Assessing real-time risk for supplier: {}", supplierId);
            
            Map<String, Object> result = analyticsService.assessRealTimeRisk(supplierId, context);
            
            logger.info("Successfully assessed real-time risk for supplier: {}", supplierId);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Failed to assess real-time risk for supplier: {}", supplierId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to assess supplier risk"));
        }
    }
    
    /**
     * Get intelligent supplier recommendations
     */
    @PostMapping("/recommendations/suppliers")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<List<SupplierRecommendation>> getSupplierRecommendations(
            @RequestBody SupplierRecommendationRequest request) {
        try {
            logger.info("Generating supplier recommendations for supplier: {}", request.getCurrentSupplierId());
            
            List<SupplierRecommendation> recommendations = 
                recommendationEngine.recommendAlternativeSuppliers(
                    request.getCurrentSupplierId(), request.getCriteria());
            
            logger.info("Generated {} supplier recommendations", recommendations.size());
            
            return ResponseEntity.ok(recommendations);
            
        } catch (Exception e) {
            logger.error("Failed to generate supplier recommendations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(List.of());
        }
    }
    
    /**
     * Get optimal route recommendations
     */
    @PostMapping("/recommendations/routes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<List<RouteRecommendation>> getRouteRecommendations(
            @RequestBody RouteOptimizationRequest request) {
        try {
            logger.info("Generating route recommendations from {} to {}", 
                request.getOrigin(), request.getDestination());
            
            List<RouteRecommendation> recommendations = 
                recommendationEngine.recommendOptimalRoutes(request);
            
            logger.info("Generated {} route recommendations", recommendations.size());
            
            return ResponseEntity.ok(recommendations);
            
        } catch (Exception e) {
            logger.error("Failed to generate route recommendations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(List.of());
        }
    }
    
    /**
     * Optimize inventory levels using advanced analytics
     */
    @PostMapping("/optimize-inventory")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<InventoryOptimizationResult> optimizeInventory(
            @RequestBody InventoryOptimizationRequest request) {
        try {
            logger.info("Optimizing inventory for product: {}", request.getProductId());
            
            InventoryOptimizationResult result = 
                recommendationEngine.optimizeInventoryLevels(request);
            
            logger.info("Successfully optimized inventory for product: {}", request.getProductId());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Failed to optimize inventory", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }
    
    /**
     * Get analytics performance metrics and insights
     */
    @GetMapping("/performance-metrics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getPerformanceMetrics() {
        try {
            logger.info("Retrieving analytics performance metrics");
            
            Map<String, Object> metrics = analyticsService.getAnalyticsPerformanceMetrics();
            
            logger.info("Successfully retrieved performance metrics");
            
            return ResponseEntity.ok(metrics);
            
        } catch (Exception e) {
            logger.error("Failed to retrieve performance metrics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve performance metrics"));
        }
    }
    
    /**
     * Get analytics summary for dashboard
     */
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<Map<String, Object>> getAnalyticsSummary() {
        try {
            logger.info("Retrieving analytics summary");
            
            // Generate real-time analytics summary
            Map<String, Object> summary = analyticsService.getAnalyticsSummary();
            
            logger.info("Successfully retrieved analytics summary");
            
            return ResponseEntity.ok(summary);
            
        } catch (Exception e) {
            logger.error("Failed to retrieve analytics summary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve analytics summary"));
        }
    }
    
    /**
     * Get risk predictions for multiple suppliers
     */
    @PostMapping("/risk-predictions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<List<RiskPrediction>> getRiskPredictions(
            @RequestBody List<Long> supplierIds,
            @RequestParam(defaultValue = "30") Integer timeHorizonDays) {
        try {
            logger.info("Generating risk predictions for {} suppliers", supplierIds.size());
            
            List<RiskPrediction> predictions = analyticsService.generateRiskPredictions(
                supplierIds, timeHorizonDays);
            
            logger.info("Generated {} risk predictions", predictions.size());
            
            return ResponseEntity.ok(predictions);
            
        } catch (Exception e) {
            logger.error("Failed to generate risk predictions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(List.of());
        }
    }
    
    /**
     * Health check endpoint for analytics service
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getAnalyticsHealth() {
        try {
            Map<String, Object> health = Map.of(
                "status", "UP",
                "service", "analytics",
                "timestamp", System.currentTimeMillis(),
                "version", "1.0.0"
            );
            
            return ResponseEntity.ok(health);
            
        } catch (Exception e) {
            logger.error("Analytics health check failed", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("status", "DOWN", "error", e.getMessage()));
        }
    }
}
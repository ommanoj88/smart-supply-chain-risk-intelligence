package com.supplychainrisk.controller;

import com.supplychainrisk.dto.*;
import com.supplychainrisk.entity.AnalyticsResult;
import com.supplychainrisk.entity.RiskPrediction;
import com.supplychainrisk.service.AdvancedAnalyticsService;
import com.supplychainrisk.service.MLPredictionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:3000")
public class AdvancedAnalyticsController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdvancedAnalyticsController.class);
    
    @Autowired
    private AdvancedAnalyticsService analyticsService;
    
    @Autowired
    private MLPredictionService mlPredictionService;
    
    /**
     * Generate comprehensive analytics analysis
     */
    @PostMapping("/comprehensive-analysis")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<AnalyticsResult> generateComprehensiveAnalysis(
            @RequestBody AnalyticsRequest request) {
        try {
            logger.info("Generating comprehensive analytics analysis");
            AnalyticsResult result = analyticsService.generateComprehensiveAnalytics(request);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Comprehensive analytics generation failed", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }
    
    /**
     * Generate advanced risk predictions
     */
    @PostMapping("/risk-predictions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<List<RiskPrediction>> generateRiskPredictions(
            @RequestBody RiskPredictionRequest request) {
        try {
            logger.info("Generating advanced risk predictions");
            List<RiskPrediction> predictions = analyticsService.generateAdvancedRiskPredictions(request);
            
            return ResponseEntity.ok(predictions);
            
        } catch (Exception e) {
            logger.error("Risk prediction generation failed", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }
    
    /**
     * Get real-time risk assessment for an entity
     */
    @GetMapping("/real-time-risk/{entityType}/{entityId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<RealTimeRiskAssessment> getRealTimeRisk(
            @PathVariable RealTimeRiskAssessment.EntityType entityType,
            @PathVariable Long entityId,
            @RequestParam(required = false) Map<String, Object> contextData) {
        try {
            RealTimeRiskAssessment assessment = analyticsService.assessRealTimeRisk(
                entityId, entityType, contextData);
            
            return ResponseEntity.ok(assessment);
            
        } catch (Exception e) {
            logger.error("Real-time risk assessment failed for {} {}", entityType, entityId, e);
            return ResponseEntity.internalServerError().body(null);
        }
    }
    
    /**
     * Generate ML prediction using advanced models
     */
    @PostMapping("/ml-prediction")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<MLPredictionResult> generateMLPrediction(
            @RequestBody MLPredictionRequest request) {
        try {
            logger.info("Generating ML prediction for type: {}", request.getPredictionType());
            
            // Convert request to appropriate format for ML service
            List<Map<String, Object>> inputData = request.getInputData();
            MLPredictionResult result;
            
            if (inputData != null && !inputData.isEmpty()) {
                result = mlPredictionService.predict(inputData, 
                    java.time.Duration.ofDays(request.getTimeHorizonDays() != null ? request.getTimeHorizonDays() : 30));
            } else {
                // Generate real-time prediction if no historical data provided
                result = mlPredictionService.generateRealTimePrediction(request);
            }
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("ML prediction generation failed", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }
    
    /**
     * Get analytics summary with key metrics
     */
    @GetMapping("/analytics-summary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<Map<String, Object>> getAnalyticsSummary(
            @RequestParam(defaultValue = "LAST_7_DAYS") String timeRange) {
        try {
            Map<String, Object> summary = generateAnalyticsSummary(timeRange);
            
            return ResponseEntity.ok(summary);
            
        } catch (Exception e) {
            logger.error("Analytics summary generation failed", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }
    
    /**
     * Perform scenario analysis
     */
    @PostMapping("/scenario-analysis")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<Map<String, Object>> performScenarioAnalysis(
            @RequestBody Map<String, Object> scenarioRequest) {
        try {
            logger.info("Performing scenario analysis: {}", scenarioRequest.get("scenarioName"));
            Map<String, Object> result = generateScenarioAnalysis(scenarioRequest);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Scenario analysis failed", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }
    
    /**
     * Get correlation analysis between variables
     */
    @GetMapping("/correlation-analysis")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<Map<String, Object>> getCorrelationAnalysis(
            @RequestParam List<String> variables,
            @RequestParam(defaultValue = "LAST_90_DAYS") String timeRange) {
        try {
            Map<String, Object> analysis = generateCorrelationAnalysis(variables, timeRange);
            
            return ResponseEntity.ok(analysis);
            
        } catch (Exception e) {
            logger.error("Correlation analysis failed", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }
    
    /**
     * Get advanced dashboard data
     */
    @PostMapping("/advanced-dashboard")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<Map<String, Object>> generateAdvancedDashboard(
            @RequestBody Map<String, Object> dashboardRequest) {
        try {
            logger.info("Generating advanced analytics dashboard");
            Map<String, Object> dashboard = generateDashboardData(dashboardRequest);
            
            return ResponseEntity.ok(dashboard);
            
        } catch (Exception e) {
            logger.error("Advanced dashboard generation failed", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }
    
    // Helper methods for generating various analytics
    
    private Map<String, Object> generateAnalyticsSummary(String timeRange) {
        Map<String, Object> summary = new java.util.HashMap<>();
        
        // Mock summary data - in real implementation, this would query actual data
        summary.put("totalAnalyticsRuns", 156);
        summary.put("averageConfidence", 82.5);
        summary.put("highRiskPredictions", 23);
        summary.put("criticalAlerts", 5);
        summary.put("modelAccuracy", 87.3);
        summary.put("timeRange", timeRange);
        summary.put("lastUpdated", java.time.LocalDateTime.now());
        
        // Risk distribution
        Map<String, Integer> riskDistribution = new java.util.HashMap<>();
        riskDistribution.put("low", 67);
        riskDistribution.put("medium", 45);
        riskDistribution.put("high", 32);
        riskDistribution.put("critical", 12);
        summary.put("riskDistribution", riskDistribution);
        
        // Top risk categories
        Map<String, Double> topRiskCategories = new java.util.HashMap<>();
        topRiskCategories.put("delivery_delays", 23.5);
        topRiskCategories.put("cost_increases", 18.7);
        topRiskCategories.put("supply_disruptions", 15.2);
        topRiskCategories.put("quality_issues", 12.8);
        summary.put("topRiskCategories", topRiskCategories);
        
        return summary;
    }
    
    private Map<String, Object> generateScenarioAnalysis(Map<String, Object> scenarioRequest) {
        Map<String, Object> analysis = new java.util.HashMap<>();
        
        String scenarioName = (String) scenarioRequest.get("scenarioName");
        analysis.put("scenarioName", scenarioName);
        analysis.put("analysisDate", java.time.LocalDateTime.now());
        
        // Mock scenario analysis results
        Map<String, Object> baselineScenario = new java.util.HashMap<>();
        baselineScenario.put("overallRisk", 45.2);
        baselineScenario.put("expectedCost", 125000.0);
        baselineScenario.put("timeToDeliver", 14);
        analysis.put("baseline", baselineScenario);
        
        Map<String, Object> stressScenario = new java.util.HashMap<>();
        stressScenario.put("overallRisk", 78.5);
        stressScenario.put("expectedCost", 187500.0);
        stressScenario.put("timeToDeliver", 21);
        analysis.put("stressTest", stressScenario);
        
        // Recommendations
        List<String> recommendations = new java.util.ArrayList<>();
        recommendations.add("Increase inventory buffer by 15%");
        recommendations.add("Diversify supplier base for critical components");
        recommendations.add("Implement enhanced monitoring for high-risk suppliers");
        analysis.put("recommendations", recommendations);
        
        return analysis;
    }
    
    private Map<String, Object> generateCorrelationAnalysis(List<String> variables, String timeRange) {
        Map<String, Object> analysis = new java.util.HashMap<>();
        
        analysis.put("variables", variables);
        analysis.put("timeRange", timeRange);
        analysis.put("analysisDate", java.time.LocalDateTime.now());
        
        // Mock correlation matrix
        Map<String, Map<String, Double>> correlationMatrix = new java.util.HashMap<>();
        for (String var1 : variables) {
            Map<String, Double> correlations = new java.util.HashMap<>();
            for (String var2 : variables) {
                if (var1.equals(var2)) {
                    correlations.put(var2, 1.0);
                } else {
                    // Generate mock correlation values
                    correlations.put(var2, Math.random() * 2 - 1); // Random between -1 and 1
                }
            }
            correlationMatrix.put(var1, correlations);
        }
        analysis.put("correlationMatrix", correlationMatrix);
        
        // Significant correlations
        List<Map<String, Object>> significantCorrelations = new java.util.ArrayList<>();
        Map<String, Object> strongCorr = new java.util.HashMap<>();
        strongCorr.put("variable1", "delivery_performance");
        strongCorr.put("variable2", "supplier_risk_score");
        strongCorr.put("correlation", -0.78);
        strongCorr.put("significance", "high");
        significantCorrelations.add(strongCorr);
        
        analysis.put("significantCorrelations", significantCorrelations);
        
        return analysis;
    }
    
    private Map<String, Object> generateDashboardData(Map<String, Object> dashboardRequest) {
        Map<String, Object> dashboard = new java.util.HashMap<>();
        
        // Executive summary
        Map<String, Object> executiveSummary = new java.util.HashMap<>();
        executiveSummary.put("overallHealthScore", 78.5);
        executiveSummary.put("totalRiskExposure", 2.3); // in millions
        executiveSummary.put("activeAlerts", 12);
        executiveSummary.put("predictiveAccuracy", 85.7);
        dashboard.put("executiveSummary", executiveSummary);
        
        // Risk distribution
        Map<String, Object> riskDistribution = new java.util.HashMap<>();
        riskDistribution.put("suppliers", Map.of("low", 45, "medium", 32, "high", 18, "critical", 5));
        riskDistribution.put("shipments", Map.of("low", 234, "medium", 89, "high", 34, "critical", 8));
        riskDistribution.put("routes", Map.of("low", 67, "medium", 23, "high", 12, "critical", 3));
        dashboard.put("riskDistribution", riskDistribution);
        
        // Performance benchmarking
        Map<String, Object> benchmarking = new java.util.HashMap<>();
        benchmarking.put("industryAverage", 65.2);
        benchmarking.put("ourPerformance", 78.5);
        benchmarking.put("topQuartile", 82.1);
        benchmarking.put("improvement", "+13.3");
        dashboard.put("performanceBenchmarking", benchmarking);
        
        // Predictive insights
        List<Map<String, Object>> insights = new java.util.ArrayList<>();
        Map<String, Object> insight1 = new java.util.HashMap<>();
        insight1.put("type", "cost_increase");
        insight1.put("probability", 68.5);
        insight1.put("impact", "medium");
        insight1.put("timeframe", "next_30_days");
        insight1.put("description", "Potential 15% cost increase in electronics components due to supply constraints");
        insights.add(insight1);
        
        dashboard.put("predictiveInsights", insights);
        
        // Recent alerts
        List<Map<String, Object>> recentAlerts = new java.util.ArrayList<>();
        Map<String, Object> alert1 = new java.util.HashMap<>();
        alert1.put("id", 1001);
        alert1.put("severity", "high");
        alert1.put("type", "supplier_financial_risk");
        alert1.put("message", "Supplier ABC Corp showing increased financial risk indicators");
        alert1.put("timestamp", java.time.LocalDateTime.now().minusHours(2));
        recentAlerts.add(alert1);
        
        dashboard.put("recentAlerts", recentAlerts);
        
        dashboard.put("lastUpdated", java.time.LocalDateTime.now());
        
        return dashboard;
    }
}
package com.supplychainrisk.controller;

import com.supplychainrisk.service.ExecutiveKPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Executive KPI Controller for comprehensive supply chain performance dashboards
 * Provides enterprise-grade KPI endpoints for executive reporting and real-time monitoring
 */
@RestController
@RequestMapping("/api/executive")
@CrossOrigin(origins = "http://localhost:3000")
public class ExecutiveKPIController {
    
    private static final Logger logger = LoggerFactory.getLogger(ExecutiveKPIController.class);
    
    @Autowired
    private ExecutiveKPIService executiveKPIService;
    
    /**
     * Get comprehensive Executive KPIs dashboard data
     */
    @GetMapping("/kpis")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<?> getExecutiveKPIs() {
        try {
            logger.info("Executive KPIs request");
            Map<String, Object> kpis = executiveKPIService.calculateExecutiveKPIs();
            
            return ResponseEntity.ok(kpis);
            
        } catch (Exception e) {
            logger.error("Executive KPIs request failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to retrieve Executive KPIs: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get Supply Chain Health Score with detailed breakdown
     */
    @GetMapping("/health-score")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<?> getSupplyChainHealthScore() {
        try {
            logger.info("Supply Chain Health Score request");
            Double healthScore = executiveKPIService.calculateSupplyChainHealthScore();
            
            Map<String, Object> response = Map.of(
                "success", true,
                "healthScore", healthScore,
                "scoreBreakdown", Map.of(
                    "supplierPerformance", 40, // Weight percentages
                    "deliveryPerformance", 40,
                    "operationalEfficiency", 20
                ),
                "interpretation", getHealthScoreInterpretation(healthScore)
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Health Score request failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to retrieve Health Score: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get financial performance summary
     */
    @GetMapping("/financial-summary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<?> getFinancialSummary() {
        try {
            logger.info("Financial Summary request");
            Map<String, Object> kpis = executiveKPIService.calculateExecutiveKPIs();
            
            if (kpis.containsKey("financial")) {
                Map<String, Object> response = Map.of(
                    "success", true,
                    "financial", kpis.get("financial"),
                    "lastUpdated", kpis.get("lastUpdated")
                );
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "error", "Financial data not available"
                ));
            }
            
        } catch (Exception e) {
            logger.error("Financial Summary request failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to retrieve Financial Summary: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get operational excellence metrics
     */
    @GetMapping("/operational-metrics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<?> getOperationalMetrics() {
        try {
            logger.info("Operational Metrics request");
            Map<String, Object> kpis = executiveKPIService.calculateExecutiveKPIs();
            
            if (kpis.containsKey("operational")) {
                Map<String, Object> response = Map.of(
                    "success", true,
                    "operational", kpis.get("operational"),
                    "lastUpdated", kpis.get("lastUpdated")
                );
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "error", "Operational data not available"
                ));
            }
            
        } catch (Exception e) {
            logger.error("Operational Metrics request failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to retrieve Operational Metrics: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get risk management dashboard data
     */
    @GetMapping("/risk-summary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<?> getRiskSummary() {
        try {
            logger.info("Risk Summary request");
            Map<String, Object> kpis = executiveKPIService.calculateExecutiveKPIs();
            
            if (kpis.containsKey("risks")) {
                Map<String, Object> response = Map.of(
                    "success", true,
                    "risks", kpis.get("risks"),
                    "lastUpdated", kpis.get("lastUpdated")
                );
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "error", "Risk data not available"
                ));
            }
            
        } catch (Exception e) {
            logger.error("Risk Summary request failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to retrieve Risk Summary: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get real-time counters for animated displays
     */
    @GetMapping("/real-time-counters")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<?> getRealTimeCounters() {
        try {
            logger.info("Real-time Counters request");
            Map<String, Object> kpis = executiveKPIService.calculateExecutiveKPIs();
            
            if (kpis.containsKey("counters")) {
                Map<String, Object> response = Map.of(
                    "success", true,
                    "counters", kpis.get("counters"),
                    "lastUpdated", kpis.get("lastUpdated")
                );
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "error", "Counter data not available"
                ));
            }
            
        } catch (Exception e) {
            logger.error("Real-time Counters request failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to retrieve Real-time Counters: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Helper method to interpret health score
     */
    private String getHealthScoreInterpretation(Double score) {
        if (score >= 90) {
            return "Excellent - Supply chain is performing optimally";
        } else if (score >= 80) {
            return "Good - Supply chain is performing well with minor issues";
        } else if (score >= 70) {
            return "Fair - Supply chain has moderate performance issues";
        } else if (score >= 60) {
            return "Poor - Supply chain needs significant improvement";
        } else {
            return "Critical - Immediate action required to address supply chain issues";
        }
    }
}
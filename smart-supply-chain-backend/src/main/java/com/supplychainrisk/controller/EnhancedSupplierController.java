package com.supplychainrisk.controller;

import com.supplychainrisk.dto.*;
import com.supplychainrisk.entity.Supplier;
import com.supplychainrisk.service.EnhancedSupplierService;
import com.supplychainrisk.service.SupplierIntelligenceService;
import com.supplychainrisk.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/suppliers/enhanced")
@Tag(name = "Enhanced Supplier Management", description = "Advanced supplier intelligence and risk management APIs")
@CrossOrigin(origins = "*")
public class EnhancedSupplierController {
    
    private static final Logger logger = LoggerFactory.getLogger(EnhancedSupplierController.class);
    
    @Autowired
    private EnhancedSupplierService enhancedSupplierService;
    
    @Autowired
    private SupplierIntelligenceService intelligenceService;
    
    @Autowired
    private SupplierService supplierService;
    
    @GetMapping("/{id}/profile")
    @Operation(summary = "Get comprehensive supplier profile with risk, performance, and intelligence data")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<SupplierProfile> getSupplierProfile(@PathVariable Long id) {
        try {
            logger.info("Retrieving comprehensive profile for supplier ID: {}", id);
            SupplierProfile profile = enhancedSupplierService.getComprehensiveSupplierProfile(id);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            logger.error("Error retrieving supplier profile for ID {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/{id}/risk-assessment")
    @Operation(summary = "Update supplier risk assessment with new risk factors")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<SupplierRiskAssessment> updateRiskAssessment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> riskFactors) {
        try {
            logger.info("Updating risk assessment for supplier ID: {}", id);
            SupplierRiskAssessment assessment = enhancedSupplierService.updateSupplierRiskScore(id, riskFactors);
            return ResponseEntity.ok(assessment);
        } catch (Exception e) {
            logger.error("Error updating risk assessment for supplier {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/{id}/performance-dashboard")
    @Operation(summary = "Get supplier performance dashboard with analytics and trends")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<SupplierPerformanceAnalytics> getPerformanceDashboard(
            @PathVariable Long id,
            @Parameter(description = "Time range for performance analysis") 
            @RequestParam(defaultValue = "LAST_12_MONTHS") SupplierPerformanceAnalytics.TimeRange timeRange) {
        try {
            logger.info("Retrieving performance dashboard for supplier ID: {} with time range: {}", id, timeRange);
            SupplierPerformanceAnalytics dashboard = 
                enhancedSupplierService.getSupplierPerformanceDashboard(id, timeRange);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            logger.error("Error retrieving performance dashboard for supplier {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/discover")
    @Operation(summary = "Discover suppliers based on specific requirements")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<List<SupplierRecommendation>> discoverSuppliers(
            @RequestBody SupplierRequirements requirements) {
        try {
            logger.info("Discovering suppliers based on requirements: {}", requirements);
            List<SupplierRecommendation> recommendations = 
                enhancedSupplierService.findAlternativeSuppliers(requirements);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            logger.error("Error discovering suppliers", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/{id}/market-intelligence")
    @Operation(summary = "Get market intelligence and competitive analysis for supplier")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<MarketIntelligence> getMarketIntelligence(@PathVariable Long id) {
        try {
            logger.info("Retrieving market intelligence for supplier ID: {}", id);
            Supplier supplier = supplierService.getSupplierEntityById(id);
            MarketIntelligence intelligence = intelligenceService.getMarketIntelligence(supplier);
            return ResponseEntity.ok(intelligence);
        } catch (Exception e) {
            logger.error("Error retrieving market intelligence for supplier {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/opportunities")
    @Operation(summary = "Identify supplier opportunities based on requirements")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<List<SupplierOpportunity>> identifyOpportunities(
            @RequestBody SupplierRequirements requirements) {
        try {
            logger.info("Identifying supplier opportunities based on requirements");
            List<SupplierOpportunity> opportunities = 
                intelligenceService.identifySupplierOpportunities(requirements);
            return ResponseEntity.ok(opportunities);
        } catch (Exception e) {
            logger.error("Error identifying supplier opportunities", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/analytics/risk-distribution")
    @Operation(summary = "Get risk distribution analytics across all suppliers")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<Map<String, Object>> getRiskDistribution() {
        try {
            logger.info("Retrieving supplier risk distribution analytics");
            Map<String, Object> riskDistribution = enhancedSupplierService.getSupplierRiskDistribution();
            return ResponseEntity.ok(riskDistribution);
        } catch (Exception e) {
            logger.error("Error retrieving risk distribution", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/analytics/performance-trends")
    @Operation(summary = "Get performance trends analytics across all suppliers")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<Map<String, Object>> getPerformanceTrends(
            @Parameter(description = "Time range for trend analysis")
            @RequestParam(defaultValue = "LAST_6_MONTHS") SupplierPerformanceAnalytics.TimeRange timeRange) {
        try {
            logger.info("Retrieving supplier performance trends for time range: {}", timeRange);
            Map<String, Object> trends = enhancedSupplierService.getSupplierPerformanceTrends(timeRange);
            return ResponseEntity.ok(trends);
        } catch (Exception e) {
            logger.error("Error retrieving performance trends", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/{id}/risk-factors")
    @Operation(summary = "Get detailed risk factors for a specific supplier")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<SupplierRiskAssessment> getRiskFactors(@PathVariable Long id) {
        try {
            logger.info("Retrieving risk factors for supplier ID: {}", id);
            SupplierProfile profile = enhancedSupplierService.getComprehensiveSupplierProfile(id);
            return ResponseEntity.ok(profile.getRiskAssessment());
        } catch (Exception e) {
            logger.error("Error retrieving risk factors for supplier {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/{id}/network")
    @Operation(summary = "Get supplier network and relationship information")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<SupplierNetwork> getSupplierNetwork(@PathVariable Long id) {
        try {
            logger.info("Retrieving supplier network for supplier ID: {}", id);
            SupplierProfile profile = enhancedSupplierService.getComprehensiveSupplierProfile(id);
            return ResponseEntity.ok(profile.getSupplierNetwork());
        } catch (Exception e) {
            logger.error("Error retrieving supplier network for supplier {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
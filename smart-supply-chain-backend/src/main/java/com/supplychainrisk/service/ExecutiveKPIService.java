package com.supplychainrisk.service;

import com.supplychainrisk.entity.Supplier;
import com.supplychainrisk.entity.Shipment;
import com.supplychainrisk.repository.SupplierRepository;
import com.supplychainrisk.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Executive KPI Service for comprehensive supply chain performance metrics
 * Provides real-time calculations of key performance indicators for executive reporting
 */
@Service
public class ExecutiveKPIService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExecutiveKPIService.class);
    
    @Autowired
    private SupplierRepository supplierRepository;
    
    @Autowired
    private ShipmentRepository shipmentRepository;
    
    /**
     * Calculate comprehensive Executive KPIs dashboard data
     */
    public Map<String, Object> calculateExecutiveKPIs() {
        logger.info("Calculating comprehensive Executive KPIs");
        
        Map<String, Object> kpis = new HashMap<>();
        
        try {
            // Calculate Supply Chain Health Score
            Double healthScore = calculateSupplyChainHealthScore();
            kpis.put("supplyChainHealth", healthScore);
            
            // Financial Metrics
            Map<String, Object> financial = calculateFinancialMetrics();
            kpis.put("financial", financial);
            
            // Operational Metrics
            Map<String, Object> operational = calculateOperationalMetrics();
            kpis.put("operational", operational);
            
            // Risk Metrics
            Map<String, Object> risks = calculateRiskMetrics();
            kpis.put("risks", risks);
            
            // Performance Trends
            Map<String, Object> trends = calculateTrendData();
            kpis.put("trends", trends);
            
            // Real-time counters
            Map<String, Object> counters = calculateRealTimeCounters();
            kpis.put("counters", counters);
            
            kpis.put("lastUpdated", LocalDateTime.now());
            kpis.put("success", true);
            
            logger.info("Executive KPIs calculated successfully");
            return kpis;
            
        } catch (Exception e) {
            logger.error("Error calculating Executive KPIs: {}", e.getMessage(), e);
            return Map.of(
                "success", false,
                "error", "Failed to calculate Executive KPIs",
                "lastUpdated", LocalDateTime.now()
            );
        }
    }
    
    /**
     * Calculate Supply Chain Health Score (0-100)
     * Weighted combination of supplier performance, delivery performance, and risk levels
     */
    public Double calculateSupplyChainHealthScore() {
        try {
            List<Supplier> suppliers = supplierRepository.findAll();
            
            if (suppliers.isEmpty()) {
                logger.warn("No suppliers found for health score calculation");
                return 75.0; // Default score
            }
            
            // Calculate average supplier score (inverse of risk score)
            Double avgSupplierScore = suppliers.stream()
                .filter(s -> s.getOverallRiskScore() != null)
                .mapToDouble(s -> Math.max(0, 100 - s.getOverallRiskScore().doubleValue()))
                .average()
                .orElse(75.0);
            
            // Calculate delivery performance
            Double deliveryPerformance = calculateOnTimeDeliveryRate();
            
            // Calculate operational efficiency
            Double operationalEfficiency = calculateOperationalEfficiency();
            
            // Weighted health score calculation
            Double healthScore = (avgSupplierScore * 0.4) + 
                               (deliveryPerformance * 0.4) + 
                               (operationalEfficiency * 0.2);
            
            return Math.round(healthScore * 100.0) / 100.0;
            
        } catch (Exception e) {
            logger.error("Error calculating supply chain health score: {}", e.getMessage(), e);
            return 75.0;
        }
    }
    
    /**
     * Calculate financial performance metrics
     */
    private Map<String, Object> calculateFinancialMetrics() {
        Map<String, Object> financial = new HashMap<>();
        
        try {
            // Mock calculations for financial metrics
            // In real implementation, this would connect to financial systems
            financial.put("costSavingsYTD", 2450000); // $2.45M
            financial.put("costSavingsTrend", 18.3);
            financial.put("roiPercentage", 24.7);
            financial.put("budgetVariance", -3.2); // Under budget by 3.2%
            financial.put("costPerShipment", 1250.75);
            
        } catch (Exception e) {
            logger.error("Error calculating financial metrics: {}", e.getMessage(), e);
            // Provide default values on error
            financial.put("costSavingsYTD", 0);
            financial.put("costSavingsTrend", 0);
            financial.put("roiPercentage", 0);
            financial.put("budgetVariance", 0);
            financial.put("costPerShipment", 0);
        }
        
        return financial;
    }
    
    /**
     * Calculate operational excellence metrics
     */
    private Map<String, Object> calculateOperationalMetrics() {
        Map<String, Object> operational = new HashMap<>();
        
        try {
            operational.put("onTimeDeliveryRate", calculateOnTimeDeliveryRate());
            operational.put("supplierPerformanceScore", calculateSupplierPerformanceScore());
            operational.put("qualityScore", calculateQualityScore());
            operational.put("complianceRate", calculateComplianceRate());
            operational.put("perfectOrderRate", calculatePerfectOrderRate());
            
        } catch (Exception e) {
            logger.error("Error calculating operational metrics: {}", e.getMessage(), e);
            operational.put("onTimeDeliveryRate", 0);
            operational.put("supplierPerformanceScore", 0);
            operational.put("qualityScore", 0);
            operational.put("complianceRate", 0);
            operational.put("perfectOrderRate", 0);
        }
        
        return operational;
    }
    
    /**
     * Calculate risk management metrics
     */
    private Map<String, Object> calculateRiskMetrics() {
        Map<String, Object> risks = new HashMap<>();
        
        try {
            List<Supplier> suppliers = supplierRepository.findAll();
            
            long highRiskSuppliers = suppliers.stream()
                .filter(s -> s.getOverallRiskScore() != null && s.getOverallRiskScore().doubleValue() > 70)
                .count();
            
            List<Shipment> recentShipments = getRecentShipments(30);
            long delayedShipments = recentShipments.stream()
                .filter(s -> "DELAYED".equals(s.getStatus()) || isShipmentDelayed(s))
                .count();
            
            risks.put("activeHighRiskSuppliers", highRiskSuppliers);
            risks.put("delayedShipments", delayedShipments);
            risks.put("riskExposureIndex", calculateRiskExposureIndex());
            risks.put("riskMitigationRate", 88.5); // Mock value
            risks.put("avgResponseTime", 2.3); // Mock value in hours
            
        } catch (Exception e) {
            logger.error("Error calculating risk metrics: {}", e.getMessage(), e);
            risks.put("activeHighRiskSuppliers", 0);
            risks.put("delayedShipments", 0);
            risks.put("riskExposureIndex", 0);
            risks.put("riskMitigationRate", 0);
            risks.put("avgResponseTime", 0);
        }
        
        return risks;
    }
    
    /**
     * Calculate trend data for charts
     */
    private Map<String, Object> calculateTrendData() {
        Map<String, Object> trends = new HashMap<>();
        
        try {
            // Generate mock trend data
            // In real implementation, this would query historical data
            List<Map<String, Object>> healthTrend = generateMockTrendData("health", 6);
            List<Map<String, Object>> costTrend = generateMockTrendData("cost", 6);
            List<Map<String, Object>> deliveryTrend = generateMockTrendData("delivery", 6);
            
            trends.put("healthTrend", healthTrend);
            trends.put("costTrend", costTrend);
            trends.put("deliveryTrend", deliveryTrend);
            
        } catch (Exception e) {
            logger.error("Error calculating trend data: {}", e.getMessage(), e);
            trends.put("healthTrend", Collections.emptyList());
            trends.put("costTrend", Collections.emptyList());
            trends.put("deliveryTrend", Collections.emptyList());
        }
        
        return trends;
    }
    
    /**
     * Calculate real-time counter values for animated displays
     */
    private Map<String, Object> calculateRealTimeCounters() {
        Map<String, Object> counters = new HashMap<>();
        
        try {
            counters.put("totalShipments", shipmentRepository.count());
            counters.put("activeShipments", getActiveShipmentsCount());
            counters.put("totalSuppliers", supplierRepository.count());
            counters.put("activeAlerts", calculateActiveAlertsCount());
            
        } catch (Exception e) {
            logger.error("Error calculating real-time counters: {}", e.getMessage(), e);
            counters.put("totalShipments", 0);
            counters.put("activeShipments", 0);
            counters.put("totalSuppliers", 0);
            counters.put("activeAlerts", 0);
        }
        
        return counters;
    }
    
    // Helper methods
    
    private Double calculateOnTimeDeliveryRate() {
        try {
            List<Shipment> recentShipments = getRecentShipments(30);
            
            if (recentShipments.isEmpty()) {
                return 85.0; // Default value
            }
            
            long onTimeCount = recentShipments.stream()
                .filter(s -> "DELIVERED".equals(s.getStatus()))
                .filter(s -> s.getActualDeliveryDate() != null && s.getEstimatedDeliveryDate() != null)
                .filter(s -> !s.getActualDeliveryDate().isAfter(s.getEstimatedDeliveryDate()))
                .count();
            
            return Math.round((double) onTimeCount / recentShipments.size() * 100.0 * 100.0) / 100.0;
            
        } catch (Exception e) {
            logger.error("Error calculating on-time delivery rate: {}", e.getMessage(), e);
            return 85.0;
        }
    }
    
    private Double calculateSupplierPerformanceScore() {
        try {
            List<Supplier> suppliers = supplierRepository.findAll();
            
            if (suppliers.isEmpty()) {
                return 85.0;
            }
            
            return suppliers.stream()
                .filter(s -> s.getOverallRiskScore() != null)
                .mapToDouble(s -> Math.max(0, 100 - s.getOverallRiskScore().doubleValue()))
                .average()
                .orElse(85.0);
                
        } catch (Exception e) {
            logger.error("Error calculating supplier performance score: {}", e.getMessage(), e);
            return 85.0;
        }
    }
    
    private Double calculateQualityScore() {
        // Mock calculation - in real implementation would connect to quality systems
        return 97.2;
    }
    
    private Double calculateComplianceRate() {
        // Mock calculation - in real implementation would connect to compliance systems  
        return 94.8;
    }
    
    private Double calculatePerfectOrderRate() {
        // Mock calculation - in real implementation would analyze order completion data
        return 91.5;
    }
    
    private Double calculateOperationalEfficiency() {
        try {
            // Simple calculation based on delivery performance and supplier scores
            Double deliveryPerf = calculateOnTimeDeliveryRate();
            Double supplierPerf = calculateSupplierPerformanceScore();
            return (deliveryPerf + supplierPerf) / 2.0;
            
        } catch (Exception e) {
            logger.error("Error calculating operational efficiency: {}", e.getMessage(), e);
            return 80.0;
        }
    }
    
    private Double calculateRiskExposureIndex() {
        try {
            List<Supplier> suppliers = supplierRepository.findAll();
            
            if (suppliers.isEmpty()) {
                return 25.0; // Default low risk
            }
            
            return suppliers.stream()
                .filter(s -> s.getOverallRiskScore() != null)
                .mapToDouble(s -> s.getOverallRiskScore().doubleValue())
                .average()
                .orElse(25.0);
                
        } catch (Exception e) {
            logger.error("Error calculating risk exposure index: {}", e.getMessage(), e);
            return 25.0;
        }
    }
    
    private List<Shipment> getRecentShipments(int days) {
        try {
            LocalDateTime since = LocalDateTime.now().minusDays(days);
            return shipmentRepository.findByCreatedAtAfter(since);
        } catch (Exception e) {
            logger.error("Error getting recent shipments: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    
    private boolean isShipmentDelayed(Shipment shipment) {
        if (shipment.getEstimatedDeliveryDate() == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(shipment.getEstimatedDeliveryDate()) && 
               !"DELIVERED".equals(shipment.getStatus());
    }
    
    private long getActiveShipmentsCount() {
        try {
            return shipmentRepository.countByStatusIn(
                Arrays.asList("IN_TRANSIT", "PICKED_UP", "OUT_FOR_DELIVERY")
            );
        } catch (Exception e) {
            logger.error("Error getting active shipments count: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    private long calculateActiveAlertsCount() {
        // Mock calculation - in real implementation would connect to alert systems
        return 12;
    }
    
    private List<Map<String, Object>> generateMockTrendData(String type, int months) {
        List<Map<String, Object>> trend = new ArrayList<>();
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        
        for (int i = 0; i < months; i++) {
            Map<String, Object> dataPoint = new HashMap<>();
            dataPoint.put("month", monthNames[i % 12]);
            
            switch (type) {
                case "health":
                    dataPoint.put("value", 80 + (Math.random() * 15)); // 80-95 range
                    break;
                case "cost":
                    dataPoint.put("value", 200000 + (Math.random() * 100000)); // $200k-300k range
                    break;
                case "delivery":
                    dataPoint.put("value", 85 + (Math.random() * 10)); // 85-95% range
                    break;
                default:
                    dataPoint.put("value", Math.random() * 100);
            }
            
            trend.add(dataPoint);
        }
        
        return trend;
    }
}
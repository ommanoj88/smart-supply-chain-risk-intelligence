package com.supplychain.risk.service;

import com.supplychain.risk.dto.DashboardStatsDto;
import com.supplychain.risk.enums.RiskLevel;
import com.supplychain.risk.enums.ShipmentStatus;
import com.supplychain.risk.enums.SupplierStatus;
import com.supplychain.risk.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class DashboardService {
    
    @Autowired
    private SupplierRepository supplierRepository;
    
    @Autowired
    private ShipmentRepository shipmentRepository;
    
    @Autowired
    private RiskAlertRepository riskAlertRepository;
    
    @Autowired
    private RiskAssessmentRepository riskAssessmentRepository;
    
    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();
        
        // Supplier Statistics
        stats.setTotalSuppliers(supplierRepository.count());
        stats.setActiveSuppliers(supplierRepository.countByStatus(SupplierStatus.ACTIVE));
        
        // Shipment Statistics
        stats.setTotalShipments(shipmentRepository.count());
        stats.setInTransitShipments(shipmentRepository.countByStatus(ShipmentStatus.IN_TRANSIT));
        stats.setDeliveredShipments(shipmentRepository.countByStatus(ShipmentStatus.DELIVERED));
        stats.setDelayedShipments(shipmentRepository.countByStatus(ShipmentStatus.DELAYED));
        stats.setPendingShipments(shipmentRepository.countByStatus(ShipmentStatus.PENDING));
        stats.setCancelledShipments(shipmentRepository.countByStatus(ShipmentStatus.CANCELLED));
        stats.setLostShipments(shipmentRepository.countByStatus(ShipmentStatus.LOST));
        stats.setOverdueShipments(shipmentRepository.countOverdueShipments());
        
        // Risk Alert Statistics
        stats.setTotalRiskAlerts(riskAlertRepository.count());
        stats.setActiveRiskAlerts(riskAlertRepository.countActiveAlertsByRiskLevel(RiskLevel.LOW) +
                                 riskAlertRepository.countActiveAlertsByRiskLevel(RiskLevel.MEDIUM) +
                                 riskAlertRepository.countActiveAlertsByRiskLevel(RiskLevel.HIGH) +
                                 riskAlertRepository.countActiveAlertsByRiskLevel(RiskLevel.CRITICAL));
        stats.setUnacknowledgedAlerts(riskAlertRepository.countUnacknowledgedActiveAlerts());
        stats.setCriticalAlerts(riskAlertRepository.countActiveAlertsByRiskLevel(RiskLevel.CRITICAL));
        stats.setHighRiskAlerts(riskAlertRepository.countActiveAlertsByRiskLevel(RiskLevel.HIGH));
        
        // Risk Distribution by Supplier
        stats.setLowRiskSuppliers(supplierRepository.countByRiskLevel(RiskLevel.LOW));
        stats.setMediumRiskSuppliers(supplierRepository.countByRiskLevel(RiskLevel.MEDIUM));
        stats.setHighRiskSuppliers(supplierRepository.countByRiskLevel(RiskLevel.HIGH));
        stats.setCriticalRiskSuppliers(supplierRepository.countByRiskLevel(RiskLevel.CRITICAL));
        
        // Performance Metrics
        BigDecimal avgRiskScore = supplierRepository.getAverageRiskScoreByStatus(SupplierStatus.ACTIVE);
        stats.setAverageRiskScore(avgRiskScore != null ? avgRiskScore : BigDecimal.ZERO);
        
        // Calculate on-time delivery rate
        long totalDelivered = stats.getDeliveredShipments();
        long totalExpected = totalDelivered + stats.getDelayedShipments() + stats.getOverdueShipments();
        if (totalExpected > 0) {
            double onTimeRate = ((double) totalDelivered / totalExpected) * 100;
            stats.setOnTimeDeliveryRate(Math.round(onTimeRate * 100.0) / 100.0);
        } else {
            stats.setOnTimeDeliveryRate(0.0);
        }
        
        // Calculate supplier performance score (based on average risk score)
        if (avgRiskScore != null && avgRiskScore.compareTo(BigDecimal.ZERO) > 0) {
            // Convert risk score (0-10, lower is better) to performance score (0-100, higher is better)
            double performanceScore = (10.0 - avgRiskScore.doubleValue()) * 10.0;
            stats.setSupplierPerformanceScore(Math.round(performanceScore * 100.0) / 100.0);
        } else {
            stats.setSupplierPerformanceScore(100.0);
        }
        
        // Recent Activity (last 7 days)
        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);
        stats.setRecentShipments(shipmentRepository.findRecentShipments(lastWeek).size());
        stats.setRecentAlerts(riskAlertRepository.findRecentAlerts(lastWeek).size());
        stats.setRecentAssessments(riskAssessmentRepository.findRecentAssessments(lastWeek).size());
        
        // Calculate total shipment value (this would need to be added to repository if needed)
        // For now, setting a placeholder
        stats.setTotalShipmentValue(BigDecimal.valueOf(5000000)); // $5M placeholder
        
        return stats;
    }
    
    public DashboardStatsDto getFilteredStats(String timeRange, String region) {
        // This method can be extended to provide filtered statistics
        // For now, returning basic stats
        return getDashboardStats();
    }
}
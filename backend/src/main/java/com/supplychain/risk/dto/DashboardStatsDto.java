package com.supplychain.risk.dto;

import java.math.BigDecimal;

public class DashboardStatsDto {
    
    private long totalSuppliers;
    private long activeSuppliers;
    private long totalShipments;
    private long inTransitShipments;
    private long deliveredShipments;
    private long delayedShipments;
    private long overdueShipments;
    private long totalRiskAlerts;
    private long activeRiskAlerts;
    private long unacknowledgedAlerts;
    private long criticalAlerts;
    private long highRiskAlerts;
    private BigDecimal averageRiskScore;
    private BigDecimal totalShipmentValue;
    private double onTimeDeliveryRate;
    private double supplierPerformanceScore;
    
    // Recent Activity Counts
    private long recentShipments;
    private long recentAlerts;
    private long recentAssessments;
    
    // Risk Distribution
    private long lowRiskSuppliers;
    private long mediumRiskSuppliers;
    private long highRiskSuppliers;
    private long criticalRiskSuppliers;
    
    // Shipment Status Distribution
    private long pendingShipments;
    private long cancelledShipments;
    private long lostShipments;
    
    // Constructors
    public DashboardStatsDto() {}
    
    // Getters and Setters
    public long getTotalSuppliers() {
        return totalSuppliers;
    }
    
    public void setTotalSuppliers(long totalSuppliers) {
        this.totalSuppliers = totalSuppliers;
    }
    
    public long getActiveSuppliers() {
        return activeSuppliers;
    }
    
    public void setActiveSuppliers(long activeSuppliers) {
        this.activeSuppliers = activeSuppliers;
    }
    
    public long getTotalShipments() {
        return totalShipments;
    }
    
    public void setTotalShipments(long totalShipments) {
        this.totalShipments = totalShipments;
    }
    
    public long getInTransitShipments() {
        return inTransitShipments;
    }
    
    public void setInTransitShipments(long inTransitShipments) {
        this.inTransitShipments = inTransitShipments;
    }
    
    public long getDeliveredShipments() {
        return deliveredShipments;
    }
    
    public void setDeliveredShipments(long deliveredShipments) {
        this.deliveredShipments = deliveredShipments;
    }
    
    public long getDelayedShipments() {
        return delayedShipments;
    }
    
    public void setDelayedShipments(long delayedShipments) {
        this.delayedShipments = delayedShipments;
    }
    
    public long getOverdueShipments() {
        return overdueShipments;
    }
    
    public void setOverdueShipments(long overdueShipments) {
        this.overdueShipments = overdueShipments;
    }
    
    public long getTotalRiskAlerts() {
        return totalRiskAlerts;
    }
    
    public void setTotalRiskAlerts(long totalRiskAlerts) {
        this.totalRiskAlerts = totalRiskAlerts;
    }
    
    public long getActiveRiskAlerts() {
        return activeRiskAlerts;
    }
    
    public void setActiveRiskAlerts(long activeRiskAlerts) {
        this.activeRiskAlerts = activeRiskAlerts;
    }
    
    public long getUnacknowledgedAlerts() {
        return unacknowledgedAlerts;
    }
    
    public void setUnacknowledgedAlerts(long unacknowledgedAlerts) {
        this.unacknowledgedAlerts = unacknowledgedAlerts;
    }
    
    public long getCriticalAlerts() {
        return criticalAlerts;
    }
    
    public void setCriticalAlerts(long criticalAlerts) {
        this.criticalAlerts = criticalAlerts;
    }
    
    public long getHighRiskAlerts() {
        return highRiskAlerts;
    }
    
    public void setHighRiskAlerts(long highRiskAlerts) {
        this.highRiskAlerts = highRiskAlerts;
    }
    
    public BigDecimal getAverageRiskScore() {
        return averageRiskScore;
    }
    
    public void setAverageRiskScore(BigDecimal averageRiskScore) {
        this.averageRiskScore = averageRiskScore;
    }
    
    public BigDecimal getTotalShipmentValue() {
        return totalShipmentValue;
    }
    
    public void setTotalShipmentValue(BigDecimal totalShipmentValue) {
        this.totalShipmentValue = totalShipmentValue;
    }
    
    public double getOnTimeDeliveryRate() {
        return onTimeDeliveryRate;
    }
    
    public void setOnTimeDeliveryRate(double onTimeDeliveryRate) {
        this.onTimeDeliveryRate = onTimeDeliveryRate;
    }
    
    public double getSupplierPerformanceScore() {
        return supplierPerformanceScore;
    }
    
    public void setSupplierPerformanceScore(double supplierPerformanceScore) {
        this.supplierPerformanceScore = supplierPerformanceScore;
    }
    
    public long getRecentShipments() {
        return recentShipments;
    }
    
    public void setRecentShipments(long recentShipments) {
        this.recentShipments = recentShipments;
    }
    
    public long getRecentAlerts() {
        return recentAlerts;
    }
    
    public void setRecentAlerts(long recentAlerts) {
        this.recentAlerts = recentAlerts;
    }
    
    public long getRecentAssessments() {
        return recentAssessments;
    }
    
    public void setRecentAssessments(long recentAssessments) {
        this.recentAssessments = recentAssessments;
    }
    
    public long getLowRiskSuppliers() {
        return lowRiskSuppliers;
    }
    
    public void setLowRiskSuppliers(long lowRiskSuppliers) {
        this.lowRiskSuppliers = lowRiskSuppliers;
    }
    
    public long getMediumRiskSuppliers() {
        return mediumRiskSuppliers;
    }
    
    public void setMediumRiskSuppliers(long mediumRiskSuppliers) {
        this.mediumRiskSuppliers = mediumRiskSuppliers;
    }
    
    public long getHighRiskSuppliers() {
        return highRiskSuppliers;
    }
    
    public void setHighRiskSuppliers(long highRiskSuppliers) {
        this.highRiskSuppliers = highRiskSuppliers;
    }
    
    public long getCriticalRiskSuppliers() {
        return criticalRiskSuppliers;
    }
    
    public void setCriticalRiskSuppliers(long criticalRiskSuppliers) {
        this.criticalRiskSuppliers = criticalRiskSuppliers;
    }
    
    public long getPendingShipments() {
        return pendingShipments;
    }
    
    public void setPendingShipments(long pendingShipments) {
        this.pendingShipments = pendingShipments;
    }
    
    public long getCancelledShipments() {
        return cancelledShipments;
    }
    
    public void setCancelledShipments(long cancelledShipments) {
        this.cancelledShipments = cancelledShipments;
    }
    
    public long getLostShipments() {
        return lostShipments;
    }
    
    public void setLostShipments(long lostShipments) {
        this.lostShipments = lostShipments;
    }
}
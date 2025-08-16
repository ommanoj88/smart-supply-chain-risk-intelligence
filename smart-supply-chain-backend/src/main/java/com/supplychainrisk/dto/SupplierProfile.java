package com.supplychainrisk.dto;

import com.supplychainrisk.entity.Supplier;
import java.time.LocalDateTime;
import java.util.List;

public class SupplierProfile {
    
    private Supplier supplier;
    private SupplierRiskAssessment riskAssessment;
    private SupplierPerformanceAnalytics performanceAnalytics;
    private MarketIntelligence marketIntelligence;
    private SupplierNetwork supplierNetwork;
    private LocalDateTime generatedAt;
    
    // Default constructor
    public SupplierProfile() {
        this.generatedAt = LocalDateTime.now();
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private SupplierProfile profile = new SupplierProfile();
        
        public Builder supplier(Supplier supplier) {
            profile.supplier = supplier;
            return this;
        }
        
        public Builder riskAssessment(SupplierRiskAssessment riskAssessment) {
            profile.riskAssessment = riskAssessment;
            return this;
        }
        
        public Builder performanceAnalytics(SupplierPerformanceAnalytics performanceAnalytics) {
            profile.performanceAnalytics = performanceAnalytics;
            return this;
        }
        
        public Builder marketIntelligence(MarketIntelligence marketIntelligence) {
            profile.marketIntelligence = marketIntelligence;
            return this;
        }
        
        public Builder supplierNetwork(SupplierNetwork supplierNetwork) {
            profile.supplierNetwork = supplierNetwork;
            return this;
        }
        
        public SupplierProfile build() {
            return profile;
        }
    }
    
    // Getters and Setters
    public Supplier getSupplier() {
        return supplier;
    }
    
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
    
    public SupplierRiskAssessment getRiskAssessment() {
        return riskAssessment;
    }
    
    public void setRiskAssessment(SupplierRiskAssessment riskAssessment) {
        this.riskAssessment = riskAssessment;
    }
    
    public SupplierPerformanceAnalytics getPerformanceAnalytics() {
        return performanceAnalytics;
    }
    
    public void setPerformanceAnalytics(SupplierPerformanceAnalytics performanceAnalytics) {
        this.performanceAnalytics = performanceAnalytics;
    }
    
    public MarketIntelligence getMarketIntelligence() {
        return marketIntelligence;
    }
    
    public void setMarketIntelligence(MarketIntelligence marketIntelligence) {
        this.marketIntelligence = marketIntelligence;
    }
    
    public SupplierNetwork getSupplierNetwork() {
        return supplierNetwork;
    }
    
    public void setSupplierNetwork(SupplierNetwork supplierNetwork) {
        this.supplierNetwork = supplierNetwork;
    }
    
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}
package com.supplychainrisk.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class InventoryOptimizationResult {
    
    private Long productId;
    private String productCode;
    private OptimalInventoryLevels optimalLevels;
    private List<String> recommendations;
    private Map<String, Object> riskMitigation;
    private BigDecimal estimatedCostSavings;
    private BigDecimal riskReduction;
    private Integer processingTimeMs;
    private BigDecimal confidence;
    private LocalDateTime generatedAt;
    
    // Default constructor
    public InventoryOptimizationResult() {}
    
    // Builder pattern
    public static class Builder {
        private InventoryOptimizationResult result = new InventoryOptimizationResult();
        
        public Builder productId(Long productId) {
            result.productId = productId;
            return this;
        }
        
        public Builder productCode(String productCode) {
            result.productCode = productCode;
            return this;
        }
        
        public Builder optimalLevels(OptimalInventoryLevels optimalLevels) {
            result.optimalLevels = optimalLevels;
            return this;
        }
        
        public Builder recommendations(List<String> recommendations) {
            result.recommendations = recommendations;
            return this;
        }
        
        public Builder riskMitigation(Map<String, Object> riskMitigation) {
            result.riskMitigation = riskMitigation;
            return this;
        }
        
        public Builder estimatedCostSavings(BigDecimal estimatedCostSavings) {
            result.estimatedCostSavings = estimatedCostSavings;
            return this;
        }
        
        public Builder riskReduction(BigDecimal riskReduction) {
            result.riskReduction = riskReduction;
            return this;
        }
        
        public Builder confidence(BigDecimal confidence) {
            result.confidence = confidence;
            return this;
        }
        
        public InventoryOptimizationResult build() {
            result.generatedAt = LocalDateTime.now();
            return result;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Nested class for optimal levels
    public static class OptimalInventoryLevels {
        private BigDecimal safetyStock;
        private BigDecimal reorderPoint;
        private BigDecimal economicOrderQuantity;
        private BigDecimal maxStock;
        private Map<String, BigDecimal> supplierAllocation;
        
        public OptimalInventoryLevels() {}
        
        // Getters and setters
        public BigDecimal getSafetyStock() {
            return safetyStock;
        }
        
        public void setSafetyStock(BigDecimal safetyStock) {
            this.safetyStock = safetyStock;
        }
        
        public BigDecimal getReorderPoint() {
            return reorderPoint;
        }
        
        public void setReorderPoint(BigDecimal reorderPoint) {
            this.reorderPoint = reorderPoint;
        }
        
        public BigDecimal getEconomicOrderQuantity() {
            return economicOrderQuantity;
        }
        
        public void setEconomicOrderQuantity(BigDecimal economicOrderQuantity) {
            this.economicOrderQuantity = economicOrderQuantity;
        }
        
        public BigDecimal getMaxStock() {
            return maxStock;
        }
        
        public void setMaxStock(BigDecimal maxStock) {
            this.maxStock = maxStock;
        }
        
        public Map<String, BigDecimal> getSupplierAllocation() {
            return supplierAllocation;
        }
        
        public void setSupplierAllocation(Map<String, BigDecimal> supplierAllocation) {
            this.supplierAllocation = supplierAllocation;
        }
    }
    
    // Getters and setters
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public String getProductCode() {
        return productCode;
    }
    
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    
    public OptimalInventoryLevels getOptimalLevels() {
        return optimalLevels;
    }
    
    public void setOptimalLevels(OptimalInventoryLevels optimalLevels) {
        this.optimalLevels = optimalLevels;
    }
    
    public List<String> getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
    
    public Map<String, Object> getRiskMitigation() {
        return riskMitigation;
    }
    
    public void setRiskMitigation(Map<String, Object> riskMitigation) {
        this.riskMitigation = riskMitigation;
    }
    
    public BigDecimal getEstimatedCostSavings() {
        return estimatedCostSavings;
    }
    
    public void setEstimatedCostSavings(BigDecimal estimatedCostSavings) {
        this.estimatedCostSavings = estimatedCostSavings;
    }
    
    public BigDecimal getRiskReduction() {
        return riskReduction;
    }
    
    public void setRiskReduction(BigDecimal riskReduction) {
        this.riskReduction = riskReduction;
    }
    
    public Integer getProcessingTimeMs() {
        return processingTimeMs;
    }
    
    public void setProcessingTimeMs(Integer processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }
    
    public BigDecimal getConfidence() {
        return confidence;
    }
    
    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }
    
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}
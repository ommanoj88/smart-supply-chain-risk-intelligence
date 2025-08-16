package com.supplychainrisk.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class SupplierRecommendation {
    
    private Long supplierId;
    private String supplierName;
    private String supplierCode;
    private BigDecimal totalScore;
    private Map<String, BigDecimal> scoreBreakdown; // Quality, Cost, Risk, Delivery, etc.
    private String recommendationType; // ALTERNATIVE, BACKUP, STRATEGIC_PARTNER
    private String reasonCode; // WHY_RECOMMENDED
    private List<String> advantages;
    private List<String> risks;
    private Map<String, Object> comparisonMetrics;
    private BigDecimal confidence;
    private LocalDateTime generatedAt;
    private Integer priority; // 1-10, lower is higher priority
    
    // Default constructor
    public SupplierRecommendation() {}
    
    // Builder pattern
    public static class Builder {
        private SupplierRecommendation recommendation = new SupplierRecommendation();
        
        public Builder supplierId(Long supplierId) {
            recommendation.supplierId = supplierId;
            return this;
        }
        
        public Builder supplierName(String supplierName) {
            recommendation.supplierName = supplierName;
            return this;
        }
        
        public Builder supplierCode(String supplierCode) {
            recommendation.supplierCode = supplierCode;
            return this;
        }
        
        public Builder totalScore(BigDecimal totalScore) {
            recommendation.totalScore = totalScore;
            return this;
        }
        
        public Builder scoreBreakdown(Map<String, BigDecimal> scoreBreakdown) {
            recommendation.scoreBreakdown = scoreBreakdown;
            return this;
        }
        
        public Builder recommendationType(String recommendationType) {
            recommendation.recommendationType = recommendationType;
            return this;
        }
        
        public Builder reasonCode(String reasonCode) {
            recommendation.reasonCode = reasonCode;
            return this;
        }
        
        public Builder advantages(List<String> advantages) {
            recommendation.advantages = advantages;
            return this;
        }
        
        public Builder risks(List<String> risks) {
            recommendation.risks = risks;
            return this;
        }
        
        public Builder comparisonMetrics(Map<String, Object> comparisonMetrics) {
            recommendation.comparisonMetrics = comparisonMetrics;
            return this;
        }
        
        public Builder confidence(BigDecimal confidence) {
            recommendation.confidence = confidence;
            return this;
        }
        
        public Builder priority(Integer priority) {
            recommendation.priority = priority;
            return this;
        }
        
        public SupplierRecommendation build() {
            recommendation.generatedAt = LocalDateTime.now();
            return recommendation;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Getters and setters
    public Long getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }
    
    public String getSupplierName() {
        return supplierName;
    }
    
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    
    public String getSupplierCode() {
        return supplierCode;
    }
    
    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }
    
    public BigDecimal getTotalScore() {
        return totalScore;
    }
    
    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }
    
    public Map<String, BigDecimal> getScoreBreakdown() {
        return scoreBreakdown;
    }
    
    public void setScoreBreakdown(Map<String, BigDecimal> scoreBreakdown) {
        this.scoreBreakdown = scoreBreakdown;
    }
    
    public String getRecommendationType() {
        return recommendationType;
    }
    
    public void setRecommendationType(String recommendationType) {
        this.recommendationType = recommendationType;
    }
    
    public String getReasonCode() {
        return reasonCode;
    }
    
    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }
    
    public List<String> getAdvantages() {
        return advantages;
    }
    
    public void setAdvantages(List<String> advantages) {
        this.advantages = advantages;
    }
    
    public List<String> getRisks() {
        return risks;
    }
    
    public void setRisks(List<String> risks) {
        this.risks = risks;
    }
    
    public Map<String, Object> getComparisonMetrics() {
        return comparisonMetrics;
    }
    
    public void setComparisonMetrics(Map<String, Object> comparisonMetrics) {
        this.comparisonMetrics = comparisonMetrics;
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
    
    public Integer getPriority() {
        return priority;
    }
    
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
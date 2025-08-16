package com.supplychainrisk.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class SupplierRecommendationRequest {
    
    private Long currentSupplierId;
    private RecommendationCriteria criteria;
    private String requestType; // ALTERNATIVE, BACKUP, DIVERSIFICATION
    private String urgencyLevel; // LOW, MEDIUM, HIGH, CRITICAL
    private Map<String, Object> businessContext;
    private List<String> requiredCapabilities;
    private BigDecimal maxRiskScore;
    private BigDecimal minQualityRating;
    private String timeframe; // IMMEDIATE, SHORT_TERM, LONG_TERM
    
    // Default constructor
    public SupplierRecommendationRequest() {}
    
    // Getters and setters
    public Long getCurrentSupplierId() {
        return currentSupplierId;
    }
    
    public void setCurrentSupplierId(Long currentSupplierId) {
        this.currentSupplierId = currentSupplierId;
    }
    
    public RecommendationCriteria getCriteria() {
        return criteria;
    }
    
    public void setCriteria(RecommendationCriteria criteria) {
        this.criteria = criteria;
    }
    
    public String getRequestType() {
        return requestType;
    }
    
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
    
    public String getUrgencyLevel() {
        return urgencyLevel;
    }
    
    public void setUrgencyLevel(String urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }
    
    public Map<String, Object> getBusinessContext() {
        return businessContext;
    }
    
    public void setBusinessContext(Map<String, Object> businessContext) {
        this.businessContext = businessContext;
    }
    
    public List<String> getRequiredCapabilities() {
        return requiredCapabilities;
    }
    
    public void setRequiredCapabilities(List<String> requiredCapabilities) {
        this.requiredCapabilities = requiredCapabilities;
    }
    
    public BigDecimal getMaxRiskScore() {
        return maxRiskScore;
    }
    
    public void setMaxRiskScore(BigDecimal maxRiskScore) {
        this.maxRiskScore = maxRiskScore;
    }
    
    public BigDecimal getMinQualityRating() {
        return minQualityRating;
    }
    
    public void setMinQualityRating(BigDecimal minQualityRating) {
        this.minQualityRating = minQualityRating;
    }
    
    public String getTimeframe() {
        return timeframe;
    }
    
    public void setTimeframe(String timeframe) {
        this.timeframe = timeframe;
    }
}
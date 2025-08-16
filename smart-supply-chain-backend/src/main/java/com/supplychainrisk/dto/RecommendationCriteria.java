package com.supplychainrisk.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class RecommendationCriteria {
    
    private Map<String, BigDecimal> weights; // Quality, Cost, Risk, Delivery weights
    private BigDecimal maxRiskThreshold;
    private BigDecimal minQualityThreshold;
    private List<String> requiredCertifications;
    private List<String> preferredCountries;
    private List<String> excludedCountries;
    private Integer maxRecommendations = 5;
    private String businessPriority; // COST_OPTIMIZATION, RISK_MINIMIZATION, QUALITY_FOCUS
    
    // Default constructor
    public RecommendationCriteria() {}
    
    // Getters and setters
    public Map<String, BigDecimal> getWeights() {
        return weights;
    }
    
    public void setWeights(Map<String, BigDecimal> weights) {
        this.weights = weights;
    }
    
    public BigDecimal getMaxRiskThreshold() {
        return maxRiskThreshold;
    }
    
    public void setMaxRiskThreshold(BigDecimal maxRiskThreshold) {
        this.maxRiskThreshold = maxRiskThreshold;
    }
    
    public BigDecimal getMinQualityThreshold() {
        return minQualityThreshold;
    }
    
    public void setMinQualityThreshold(BigDecimal minQualityThreshold) {
        this.minQualityThreshold = minQualityThreshold;
    }
    
    public List<String> getRequiredCertifications() {
        return requiredCertifications;
    }
    
    public void setRequiredCertifications(List<String> requiredCertifications) {
        this.requiredCertifications = requiredCertifications;
    }
    
    public List<String> getPreferredCountries() {
        return preferredCountries;
    }
    
    public void setPreferredCountries(List<String> preferredCountries) {
        this.preferredCountries = preferredCountries;
    }
    
    public List<String> getExcludedCountries() {
        return excludedCountries;
    }
    
    public void setExcludedCountries(List<String> excludedCountries) {
        this.excludedCountries = excludedCountries;
    }
    
    public Integer getMaxRecommendations() {
        return maxRecommendations;
    }
    
    public void setMaxRecommendations(Integer maxRecommendations) {
        this.maxRecommendations = maxRecommendations;
    }
    
    public String getBusinessPriority() {
        return businessPriority;
    }
    
    public void setBusinessPriority(String businessPriority) {
        this.businessPriority = businessPriority;
    }
}
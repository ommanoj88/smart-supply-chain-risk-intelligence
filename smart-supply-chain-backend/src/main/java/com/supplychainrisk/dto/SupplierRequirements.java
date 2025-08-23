package com.supplychainrisk.dto;

import java.util.List;

public class SupplierRequirements {
    
    private List<String> requiredCapabilities;
    private List<String> preferredLocations;
    private String maxRiskLevel;
    private Double minQualityScore;
    private Double minOnTimeDeliveryRate;
    private String industry;
    private List<String> requiredCertifications;
    private String supplierTier;
    private Integer maxResults;
    private String sortBy;
    private String sortDirection;
    
    // Default constructor
    public SupplierRequirements() {}
    
    // Getters and Setters
    public List<String> getRequiredCapabilities() {
        return requiredCapabilities;
    }
    
    public void setRequiredCapabilities(List<String> requiredCapabilities) {
        this.requiredCapabilities = requiredCapabilities;
    }
    
    public List<String> getPreferredLocations() {
        return preferredLocations;
    }
    
    public void setPreferredLocations(List<String> preferredLocations) {
        this.preferredLocations = preferredLocations;
    }
    
    public String getMaxRiskLevel() {
        return maxRiskLevel;
    }
    
    public void setMaxRiskLevel(String maxRiskLevel) {
        this.maxRiskLevel = maxRiskLevel;
    }
    
    public Double getMinQualityScore() {
        return minQualityScore;
    }
    
    public void setMinQualityScore(Double minQualityScore) {
        this.minQualityScore = minQualityScore;
    }
    
    public Double getMinOnTimeDeliveryRate() {
        return minOnTimeDeliveryRate;
    }
    
    public void setMinOnTimeDeliveryRate(Double minOnTimeDeliveryRate) {
        this.minOnTimeDeliveryRate = minOnTimeDeliveryRate;
    }
    
    public String getIndustry() {
        return industry;
    }
    
    public void setIndustry(String industry) {
        this.industry = industry;
    }
    
    public List<String> getRequiredCertifications() {
        return requiredCertifications;
    }
    
    public void setRequiredCertifications(List<String> requiredCertifications) {
        this.requiredCertifications = requiredCertifications;
    }
    
    public String getSupplierTier() {
        return supplierTier;
    }
    
    public void setSupplierTier(String supplierTier) {
        this.supplierTier = supplierTier;
    }
    
    public Integer getMaxResults() {
        return maxResults;
    }
    
    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public String getSortDirection() {
        return sortDirection;
    }
    
    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
}
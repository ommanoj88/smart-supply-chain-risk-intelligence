package com.supplychainrisk.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SupplierOpportunity {
    
    private String opportunityType;
    private String title;
    private String description;
    private String priority; // HIGH, MEDIUM, LOW
    private Double potentialValue;
    private String valueType; // COST_SAVINGS, REVENUE_INCREASE, RISK_REDUCTION
    private Integer estimatedTimeframe; // in months
    private List<String> requiredActions;
    private Double feasibilityScore;
    private String riskLevel; // LOW, MEDIUM, HIGH
    private LocalDateTime identifiedAt;
    
    // Default constructor
    public SupplierOpportunity() {
        this.identifiedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getOpportunityType() {
        return opportunityType;
    }
    
    public void setOpportunityType(String opportunityType) {
        this.opportunityType = opportunityType;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public Double getPotentialValue() {
        return potentialValue;
    }
    
    public void setPotentialValue(Double potentialValue) {
        this.potentialValue = potentialValue;
    }
    
    public String getValueType() {
        return valueType;
    }
    
    public void setValueType(String valueType) {
        this.valueType = valueType;
    }
    
    public Integer getEstimatedTimeframe() {
        return estimatedTimeframe;
    }
    
    public void setEstimatedTimeframe(Integer estimatedTimeframe) {
        this.estimatedTimeframe = estimatedTimeframe;
    }
    
    public List<String> getRequiredActions() {
        return requiredActions;
    }
    
    public void setRequiredActions(List<String> requiredActions) {
        this.requiredActions = requiredActions;
    }
    
    public Double getFeasibilityScore() {
        return feasibilityScore;
    }
    
    public void setFeasibilityScore(Double feasibilityScore) {
        this.feasibilityScore = feasibilityScore;
    }
    
    public String getRiskLevel() {
        return riskLevel;
    }
    
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
    
    public LocalDateTime getIdentifiedAt() {
        return identifiedAt;
    }
    
    public void setIdentifiedAt(LocalDateTime identifiedAt) {
        this.identifiedAt = identifiedAt;
    }
}
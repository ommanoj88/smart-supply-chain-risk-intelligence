package com.supplychainrisk.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class RouteOptimizationRequest {
    
    private String origin;
    private String destination;
    private String requestId;
    private Map<String, Object> constraints;
    private RecommendationCriteria criteria;
    private Integer maxRecommendations = 5;
    private LocalDate requiredDeliveryDate;
    private BigDecimal maxBudget;
    private List<String> preferredCarriers;
    private List<String> excludedCarriers;
    private String urgencyLevel; // LOW, MEDIUM, HIGH, CRITICAL
    private Map<String, Object> cargoDetails;
    
    // Default constructor
    public RouteOptimizationRequest() {}
    
    // Getters and setters
    public String getOrigin() {
        return origin;
    }
    
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public Map<String, Object> getConstraints() {
        return constraints;
    }
    
    public void setConstraints(Map<String, Object> constraints) {
        this.constraints = constraints;
    }
    
    public RecommendationCriteria getCriteria() {
        return criteria;
    }
    
    public void setCriteria(RecommendationCriteria criteria) {
        this.criteria = criteria;
    }
    
    public Integer getMaxRecommendations() {
        return maxRecommendations;
    }
    
    public void setMaxRecommendations(Integer maxRecommendations) {
        this.maxRecommendations = maxRecommendations;
    }
    
    public LocalDate getRequiredDeliveryDate() {
        return requiredDeliveryDate;
    }
    
    public void setRequiredDeliveryDate(LocalDate requiredDeliveryDate) {
        this.requiredDeliveryDate = requiredDeliveryDate;
    }
    
    public BigDecimal getMaxBudget() {
        return maxBudget;
    }
    
    public void setMaxBudget(BigDecimal maxBudget) {
        this.maxBudget = maxBudget;
    }
    
    public List<String> getPreferredCarriers() {
        return preferredCarriers;
    }
    
    public void setPreferredCarriers(List<String> preferredCarriers) {
        this.preferredCarriers = preferredCarriers;
    }
    
    public List<String> getExcludedCarriers() {
        return excludedCarriers;
    }
    
    public void setExcludedCarriers(List<String> excludedCarriers) {
        this.excludedCarriers = excludedCarriers;
    }
    
    public String getUrgencyLevel() {
        return urgencyLevel;
    }
    
    public void setUrgencyLevel(String urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }
    
    public Map<String, Object> getCargoDetails() {
        return cargoDetails;
    }
    
    public void setCargoDetails(Map<String, Object> cargoDetails) {
        this.cargoDetails = cargoDetails;
    }
}
package com.supplychainrisk.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class RouteRecommendation {
    
    private String routeId;
    private String origin;
    private String destination;
    private List<String> transitPoints;
    private String transportMode; // OCEAN, AIR, GROUND, MULTIMODAL
    private BigDecimal totalScore;
    private Map<String, BigDecimal> scoreBreakdown; // Cost, Speed, Reliability, Risk
    private BigDecimal estimatedCost;
    private Integer estimatedTransitDays;
    private BigDecimal reliabilityScore;
    private BigDecimal riskScore;
    private List<String> advantages;
    private List<String> risks;
    private String carrierName;
    private BigDecimal confidence;
    private LocalDateTime generatedAt;
    
    // Default constructor
    public RouteRecommendation() {}
    
    // Getters and setters
    public String getRouteId() {
        return routeId;
    }
    
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
    
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
    
    public List<String> getTransitPoints() {
        return transitPoints;
    }
    
    public void setTransitPoints(List<String> transitPoints) {
        this.transitPoints = transitPoints;
    }
    
    public String getTransportMode() {
        return transportMode;
    }
    
    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
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
    
    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }
    
    public void setEstimatedCost(BigDecimal estimatedCost) {
        this.estimatedCost = estimatedCost;
    }
    
    public Integer getEstimatedTransitDays() {
        return estimatedTransitDays;
    }
    
    public void setEstimatedTransitDays(Integer estimatedTransitDays) {
        this.estimatedTransitDays = estimatedTransitDays;
    }
    
    public BigDecimal getReliabilityScore() {
        return reliabilityScore;
    }
    
    public void setReliabilityScore(BigDecimal reliabilityScore) {
        this.reliabilityScore = reliabilityScore;
    }
    
    public BigDecimal getRiskScore() {
        return riskScore;
    }
    
    public void setRiskScore(BigDecimal riskScore) {
        this.riskScore = riskScore;
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
    
    public String getCarrierName() {
        return carrierName;
    }
    
    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
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
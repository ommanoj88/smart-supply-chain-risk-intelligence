package com.supplychainrisk.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class RiskPrediction {
    private Long supplierId;
    private Map<String, BigDecimal> riskScores; // Current and predicted risk scores
    private Map<String, Object> riskFactors;
    private BigDecimal overallRiskTrend; // Positive means increasing risk
    private List<String> riskAlerts;
    private BigDecimal confidence;
    private LocalDateTime predictedAt;
    private Integer timeHorizonDays;
    
    // Default constructor
    public RiskPrediction() {}
    
    // Getters and setters
    public Long getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }
    
    public Map<String, BigDecimal> getRiskScores() {
        return riskScores;
    }
    
    public void setRiskScores(Map<String, BigDecimal> riskScores) {
        this.riskScores = riskScores;
    }
    
    public Map<String, Object> getRiskFactors() {
        return riskFactors;
    }
    
    public void setRiskFactors(Map<String, Object> riskFactors) {
        this.riskFactors = riskFactors;
    }
    
    public BigDecimal getOverallRiskTrend() {
        return overallRiskTrend;
    }
    
    public void setOverallRiskTrend(BigDecimal overallRiskTrend) {
        this.overallRiskTrend = overallRiskTrend;
    }
    
    public List<String> getRiskAlerts() {
        return riskAlerts;
    }
    
    public void setRiskAlerts(List<String> riskAlerts) {
        this.riskAlerts = riskAlerts;
    }
    
    public BigDecimal getConfidence() {
        return confidence;
    }
    
    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }
    
    public LocalDateTime getPredictedAt() {
        return predictedAt;
    }
    
    public void setPredictedAt(LocalDateTime predictedAt) {
        this.predictedAt = predictedAt;
    }
    
    public Integer getTimeHorizonDays() {
        return timeHorizonDays;
    }
    
    public void setTimeHorizonDays(Integer timeHorizonDays) {
        this.timeHorizonDays = timeHorizonDays;
    }
}
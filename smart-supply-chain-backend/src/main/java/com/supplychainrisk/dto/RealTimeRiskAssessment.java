package com.supplychainrisk.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class RealTimeRiskAssessment {
    
    private Long entityId;
    private EntityType entityType;
    private BigDecimal currentRiskScore;
    private RiskLevel riskLevel;
    private BigDecimal confidence;
    private List<RiskFactor> riskFactors;
    private MLPredictionResult predictions;
    private List<MitigationRecommendation> recommendations;
    private LocalDateTime assessmentTime;
    private String dataFreshness;
    
    // Risk change indicators
    private BigDecimal riskScoreChange24h;
    private String riskTrend;
    private List<String> alertsTriggered;
    
    // Context information
    private Map<String, Object> contextData;
    private String assessmentSource;
    private String version;
    
    public enum EntityType {
        SUPPLIER,
        SHIPMENT,
        ROUTE,
        PRODUCT,
        REGION
    }
    
    public enum RiskLevel {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL;
        
        public static RiskLevel fromScore(Double score) {
            if (score == null) return MEDIUM;
            if (score < 25) return LOW;
            if (score < 50) return MEDIUM;
            if (score < 75) return HIGH;
            return CRITICAL;
        }
    }
    
    public static class RiskFactor {
        private String factorName;
        private String factorType;
        private BigDecimal impact;
        private BigDecimal probability;
        private String description;
        private Map<String, Object> metadata;
        
        // Constructors
        public RiskFactor() {}
        
        public RiskFactor(String factorName, String factorType, BigDecimal impact, BigDecimal probability) {
            this.factorName = factorName;
            this.factorType = factorType;
            this.impact = impact;
            this.probability = probability;
        }
        
        // Getters and Setters
        public String getFactorName() { return factorName; }
        public void setFactorName(String factorName) { this.factorName = factorName; }
        
        public String getFactorType() { return factorType; }
        public void setFactorType(String factorType) { this.factorType = factorType; }
        
        public BigDecimal getImpact() { return impact; }
        public void setImpact(BigDecimal impact) { this.impact = impact; }
        
        public BigDecimal getProbability() { return probability; }
        public void setProbability(BigDecimal probability) { this.probability = probability; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    public static class MitigationRecommendation {
        private String action;
        private String priority;
        private BigDecimal estimatedImpact;
        private Integer implementationDays;
        private BigDecimal cost;
        private String responsible;
        private Map<String, Object> details;
        
        // Constructors
        public MitigationRecommendation() {}
        
        public MitigationRecommendation(String action, String priority, BigDecimal estimatedImpact) {
            this.action = action;
            this.priority = priority;
            this.estimatedImpact = estimatedImpact;
        }
        
        // Getters and Setters
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        
        public BigDecimal getEstimatedImpact() { return estimatedImpact; }
        public void setEstimatedImpact(BigDecimal estimatedImpact) { this.estimatedImpact = estimatedImpact; }
        
        public Integer getImplementationDays() { return implementationDays; }
        public void setImplementationDays(Integer implementationDays) { this.implementationDays = implementationDays; }
        
        public BigDecimal getCost() { return cost; }
        public void setCost(BigDecimal cost) { this.cost = cost; }
        
        public String getResponsible() { return responsible; }
        public void setResponsible(String responsible) { this.responsible = responsible; }
        
        public Map<String, Object> getDetails() { return details; }
        public void setDetails(Map<String, Object> details) { this.details = details; }
    }
    
    // Builder pattern
    public static class Builder {
        private RealTimeRiskAssessment assessment = new RealTimeRiskAssessment();
        
        public Builder entityId(Long entityId) {
            assessment.entityId = entityId;
            return this;
        }
        
        public Builder entityType(EntityType entityType) {
            assessment.entityType = entityType;
            return this;
        }
        
        public Builder currentRiskScore(BigDecimal currentRiskScore) {
            assessment.currentRiskScore = currentRiskScore;
            return this;
        }
        
        public Builder riskLevel(RiskLevel riskLevel) {
            assessment.riskLevel = riskLevel;
            return this;
        }
        
        public Builder confidence(BigDecimal confidence) {
            assessment.confidence = confidence;
            return this;
        }
        
        public Builder riskFactors(List<RiskFactor> riskFactors) {
            assessment.riskFactors = riskFactors;
            return this;
        }
        
        public Builder predictions(MLPredictionResult predictions) {
            assessment.predictions = predictions;
            return this;
        }
        
        public Builder recommendations(List<MitigationRecommendation> recommendations) {
            assessment.recommendations = recommendations;
            return this;
        }
        
        public Builder assessmentTime(LocalDateTime assessmentTime) {
            assessment.assessmentTime = assessmentTime;
            return this;
        }
        
        public Builder dataFreshness(String dataFreshness) {
            assessment.dataFreshness = dataFreshness;
            return this;
        }
        
        public RealTimeRiskAssessment build() {
            return assessment;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Default constructor
    public RealTimeRiskAssessment() {}
    
    // Getters and Setters
    public Long getEntityId() {
        return entityId;
    }
    
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
    
    public EntityType getEntityType() {
        return entityType;
    }
    
    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }
    
    public BigDecimal getCurrentRiskScore() {
        return currentRiskScore;
    }
    
    public void setCurrentRiskScore(BigDecimal currentRiskScore) {
        this.currentRiskScore = currentRiskScore;
    }
    
    public RiskLevel getRiskLevel() {
        return riskLevel;
    }
    
    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }
    
    public BigDecimal getConfidence() {
        return confidence;
    }
    
    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }
    
    public List<RiskFactor> getRiskFactors() {
        return riskFactors;
    }
    
    public void setRiskFactors(List<RiskFactor> riskFactors) {
        this.riskFactors = riskFactors;
    }
    
    public MLPredictionResult getPredictions() {
        return predictions;
    }
    
    public void setPredictions(MLPredictionResult predictions) {
        this.predictions = predictions;
    }
    
    public List<MitigationRecommendation> getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(List<MitigationRecommendation> recommendations) {
        this.recommendations = recommendations;
    }
    
    public LocalDateTime getAssessmentTime() {
        return assessmentTime;
    }
    
    public void setAssessmentTime(LocalDateTime assessmentTime) {
        this.assessmentTime = assessmentTime;
    }
    
    public String getDataFreshness() {
        return dataFreshness;
    }
    
    public void setDataFreshness(String dataFreshness) {
        this.dataFreshness = dataFreshness;
    }
    
    public BigDecimal getRiskScoreChange24h() {
        return riskScoreChange24h;
    }
    
    public void setRiskScoreChange24h(BigDecimal riskScoreChange24h) {
        this.riskScoreChange24h = riskScoreChange24h;
    }
    
    public String getRiskTrend() {
        return riskTrend;
    }
    
    public void setRiskTrend(String riskTrend) {
        this.riskTrend = riskTrend;
    }
    
    public List<String> getAlertsTriggered() {
        return alertsTriggered;
    }
    
    public void setAlertsTriggered(List<String> alertsTriggered) {
        this.alertsTriggered = alertsTriggered;
    }
    
    public Map<String, Object> getContextData() {
        return contextData;
    }
    
    public void setContextData(Map<String, Object> contextData) {
        this.contextData = contextData;
    }
    
    public String getAssessmentSource() {
        return assessmentSource;
    }
    
    public void setAssessmentSource(String assessmentSource) {
        this.assessmentSource = assessmentSource;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
}
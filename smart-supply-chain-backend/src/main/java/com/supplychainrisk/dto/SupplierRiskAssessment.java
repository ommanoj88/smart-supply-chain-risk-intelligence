package com.supplychainrisk.dto;

import com.supplychainrisk.entity.Supplier;
import com.supplychainrisk.entity.RiskFactor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class SupplierRiskAssessment {
    
    private Supplier supplier;
    private Double riskScore;
    private Supplier.RiskLevel riskLevel;
    private List<RiskFactor> riskFactors;
    private Map<String, Object> riskBreakdown;
    private List<String> recommendations;
    private List<AlertInfo> alerts;
    private LocalDateTime assessmentDate;
    private Double confidence;
    
    // Default constructor
    public SupplierRiskAssessment() {
        this.assessmentDate = LocalDateTime.now();
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private SupplierRiskAssessment assessment = new SupplierRiskAssessment();
        
        public Builder supplier(Supplier supplier) {
            assessment.supplier = supplier;
            return this;
        }
        
        public Builder riskScore(Double riskScore) {
            assessment.riskScore = riskScore;
            return this;
        }
        
        public Builder riskLevel(Supplier.RiskLevel riskLevel) {
            assessment.riskLevel = riskLevel;
            return this;
        }
        
        public Builder riskFactors(List<RiskFactor> riskFactors) {
            assessment.riskFactors = riskFactors;
            return this;
        }
        
        public Builder riskBreakdown(Map<String, Object> riskBreakdown) {
            assessment.riskBreakdown = riskBreakdown;
            return this;
        }
        
        public Builder recommendations(List<String> recommendations) {
            assessment.recommendations = recommendations;
            return this;
        }
        
        public Builder alerts(List<AlertInfo> alerts) {
            assessment.alerts = alerts;
            return this;
        }
        
        public Builder confidence(Double confidence) {
            assessment.confidence = confidence;
            return this;
        }
        
        public SupplierRiskAssessment build() {
            return assessment;
        }
    }
    
    // Inner class for alert information
    public static class AlertInfo {
        private String type;
        private String severity;
        private String message;
        private LocalDateTime timestamp;
        
        public AlertInfo() {}
        
        public AlertInfo(String type, String severity, String message) {
            this.type = type;
            this.severity = severity;
            this.message = message;
            this.timestamp = LocalDateTime.now();
        }
        
        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
    
    // Getters and Setters
    public Supplier getSupplier() {
        return supplier;
    }
    
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
    
    public Double getRiskScore() {
        return riskScore;
    }
    
    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }
    
    public Supplier.RiskLevel getRiskLevel() {
        return riskLevel;
    }
    
    public void setRiskLevel(Supplier.RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }
    
    public List<RiskFactor> getRiskFactors() {
        return riskFactors;
    }
    
    public void setRiskFactors(List<RiskFactor> riskFactors) {
        this.riskFactors = riskFactors;
    }
    
    public Map<String, Object> getRiskBreakdown() {
        return riskBreakdown;
    }
    
    public void setRiskBreakdown(Map<String, Object> riskBreakdown) {
        this.riskBreakdown = riskBreakdown;
    }
    
    public List<String> getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
    
    public List<AlertInfo> getAlerts() {
        return alerts;
    }
    
    public void setAlerts(List<AlertInfo> alerts) {
        this.alerts = alerts;
    }
    
    public LocalDateTime getAssessmentDate() {
        return assessmentDate;
    }
    
    public void setAssessmentDate(LocalDateTime assessmentDate) {
        this.assessmentDate = assessmentDate;
    }
    
    public Double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
}
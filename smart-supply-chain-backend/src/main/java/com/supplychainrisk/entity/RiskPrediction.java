package com.supplychainrisk.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_predictions")
public class RiskPrediction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Prediction Details
    @Column(name = "risk_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private RiskType riskType;
    
    @Column(name = "risk_probability", precision = 5, scale = 2)
    private BigDecimal riskProbability;
    
    @Column(name = "predicted_risk_level", length = 20)
    @Enumerated(EnumType.STRING)
    private RiskLevel predictedRiskLevel;
    
    @Column(name = "confidence", precision = 5, scale = 2)
    private BigDecimal confidence;
    
    // Time Information
    @Column(name = "prediction_date", nullable = false)
    private LocalDateTime predictionDate;
    
    @Column(name = "event_predicted_date")
    private LocalDateTime eventPredictedDate;
    
    @Column(name = "prediction_horizon_days")
    private Integer predictionHorizonDays;
    
    // Impact Assessment
    @Column(name = "estimated_cost_impact", precision = 12, scale = 2)
    private BigDecimal estimatedCostImpact;
    
    @Column(name = "estimated_delay_days")
    private Integer estimatedDelayDays;
    
    @Column(name = "impact_severity", length = 20)
    @Enumerated(EnumType.STRING)
    private ImpactSeverity impactSeverity;
    
    // Contributing Factors (JSON format)
    @Column(name = "risk_factors", columnDefinition = "TEXT")
    private String riskFactors;
    
    // Model Information
    @Column(name = "model_id", length = 100)
    private String modelId;
    
    @Column(name = "model_version", length = 20)
    private String modelVersion;
    
    @Column(name = "model_accuracy", precision = 5, scale = 2)
    private BigDecimal modelAccuracy;
    
    // Mitigation Recommendations (JSON format)
    @Column(name = "mitigation_strategies", columnDefinition = "TEXT")
    private String mitigationStrategies;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;
    
    // Audit Fields
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        predictionDate = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Enums
    public enum RiskType {
        DELAY_PREDICTION,
        COST_INCREASE,
        SUPPLY_DISRUPTION,
        DEMAND_FORECAST,
        RISK_EVENT,
        QUALITY_ISSUE,
        COMPLIANCE_RISK
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
    
    public enum ImpactSeverity {
        MINIMAL,
        MODERATE,
        SIGNIFICANT,
        SEVERE,
        CATASTROPHIC
    }
    
    // Default constructor
    public RiskPrediction() {}
    
    // Builder pattern
    public static class Builder {
        private RiskPrediction prediction = new RiskPrediction();
        
        public Builder riskType(RiskType riskType) {
            prediction.riskType = riskType;
            return this;
        }
        
        public Builder riskProbability(BigDecimal riskProbability) {
            prediction.riskProbability = riskProbability;
            return this;
        }
        
        public Builder predictedRiskLevel(RiskLevel predictedRiskLevel) {
            prediction.predictedRiskLevel = predictedRiskLevel;
            return this;
        }
        
        public Builder confidence(BigDecimal confidence) {
            prediction.confidence = confidence;
            return this;
        }
        
        public Builder eventPredictedDate(LocalDateTime eventPredictedDate) {
            prediction.eventPredictedDate = eventPredictedDate;
            return this;
        }
        
        public Builder predictionHorizonDays(Integer predictionHorizonDays) {
            prediction.predictionHorizonDays = predictionHorizonDays;
            return this;
        }
        
        public Builder estimatedCostImpact(BigDecimal estimatedCostImpact) {
            prediction.estimatedCostImpact = estimatedCostImpact;
            return this;
        }
        
        public Builder estimatedDelayDays(Integer estimatedDelayDays) {
            prediction.estimatedDelayDays = estimatedDelayDays;
            return this;
        }
        
        public Builder impactSeverity(ImpactSeverity impactSeverity) {
            prediction.impactSeverity = impactSeverity;
            return this;
        }
        
        public Builder riskFactors(String riskFactors) {
            prediction.riskFactors = riskFactors;
            return this;
        }
        
        public Builder modelId(String modelId) {
            prediction.modelId = modelId;
            return this;
        }
        
        public Builder modelVersion(String modelVersion) {
            prediction.modelVersion = modelVersion;
            return this;
        }
        
        public Builder modelAccuracy(BigDecimal modelAccuracy) {
            prediction.modelAccuracy = modelAccuracy;
            return this;
        }
        
        public Builder mitigationStrategies(String mitigationStrategies) {
            prediction.mitigationStrategies = mitigationStrategies;
            return this;
        }
        
        public Builder supplier(Supplier supplier) {
            prediction.supplier = supplier;
            return this;
        }
        
        public Builder shipment(Shipment shipment) {
            prediction.shipment = shipment;
            return this;
        }
        
        public RiskPrediction build() {
            return prediction;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public RiskType getRiskType() {
        return riskType;
    }
    
    public void setRiskType(RiskType riskType) {
        this.riskType = riskType;
    }
    
    public BigDecimal getRiskProbability() {
        return riskProbability;
    }
    
    public void setRiskProbability(BigDecimal riskProbability) {
        this.riskProbability = riskProbability;
    }
    
    public RiskLevel getPredictedRiskLevel() {
        return predictedRiskLevel;
    }
    
    public void setPredictedRiskLevel(RiskLevel predictedRiskLevel) {
        this.predictedRiskLevel = predictedRiskLevel;
    }
    
    public BigDecimal getConfidence() {
        return confidence;
    }
    
    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }
    
    public LocalDateTime getPredictionDate() {
        return predictionDate;
    }
    
    public void setPredictionDate(LocalDateTime predictionDate) {
        this.predictionDate = predictionDate;
    }
    
    public LocalDateTime getEventPredictedDate() {
        return eventPredictedDate;
    }
    
    public void setEventPredictedDate(LocalDateTime eventPredictedDate) {
        this.eventPredictedDate = eventPredictedDate;
    }
    
    public Integer getPredictionHorizonDays() {
        return predictionHorizonDays;
    }
    
    public void setPredictionHorizonDays(Integer predictionHorizonDays) {
        this.predictionHorizonDays = predictionHorizonDays;
    }
    
    public BigDecimal getEstimatedCostImpact() {
        return estimatedCostImpact;
    }
    
    public void setEstimatedCostImpact(BigDecimal estimatedCostImpact) {
        this.estimatedCostImpact = estimatedCostImpact;
    }
    
    public Integer getEstimatedDelayDays() {
        return estimatedDelayDays;
    }
    
    public void setEstimatedDelayDays(Integer estimatedDelayDays) {
        this.estimatedDelayDays = estimatedDelayDays;
    }
    
    public ImpactSeverity getImpactSeverity() {
        return impactSeverity;
    }
    
    public void setImpactSeverity(ImpactSeverity impactSeverity) {
        this.impactSeverity = impactSeverity;
    }
    
    public String getRiskFactors() {
        return riskFactors;
    }
    
    public void setRiskFactors(String riskFactors) {
        this.riskFactors = riskFactors;
    }
    
    public String getModelId() {
        return modelId;
    }
    
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }
    
    public String getModelVersion() {
        return modelVersion;
    }
    
    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }
    
    public BigDecimal getModelAccuracy() {
        return modelAccuracy;
    }
    
    public void setModelAccuracy(BigDecimal modelAccuracy) {
        this.modelAccuracy = modelAccuracy;
    }
    
    public String getMitigationStrategies() {
        return mitigationStrategies;
    }
    
    public void setMitigationStrategies(String mitigationStrategies) {
        this.mitigationStrategies = mitigationStrategies;
    }
    
    public Supplier getSupplier() {
        return supplier;
    }
    
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
    
    public Shipment getShipment() {
        return shipment;
    }
    
    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
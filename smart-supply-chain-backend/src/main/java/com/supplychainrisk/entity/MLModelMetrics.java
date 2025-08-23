package com.supplychainrisk.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ml_model_metrics")
public class MLModelMetrics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Model Information
    @Column(name = "model_id", nullable = false, length = 100)
    private String modelId;
    
    @Column(name = "model_name", nullable = false, length = 200)
    private String modelName;
    
    @Column(name = "model_version", nullable = false, length = 20)
    private String modelVersion;
    
    @Column(name = "model_type", length = 50)
    @Enumerated(EnumType.STRING)
    private ModelType modelType;
    
    // Performance Metrics
    @Column(name = "accuracy", precision = 7, scale = 5)
    private BigDecimal accuracy;
    
    @Column(name = "precision", precision = 7, scale = 5)
    private BigDecimal precision;
    
    @Column(name = "recall", precision = 7, scale = 5)
    private BigDecimal recall;
    
    @Column(name = "f1_score", precision = 7, scale = 5)
    private BigDecimal f1Score;
    
    @Column(name = "auc", precision = 7, scale = 5)
    private BigDecimal auc;
    
    // Operational Metrics
    @Column(name = "average_latency_ms")
    private Long averageLatencyMs;
    
    @Column(name = "throughput_per_second")
    private Long throughputPerSecond;
    
    @Column(name = "resource_utilization", precision = 5, scale = 2)
    private BigDecimal resourceUtilization;
    
    // Data Quality Metrics
    @Column(name = "data_quality_score", precision = 5, scale = 2)
    private BigDecimal dataQualityScore;
    
    @Column(name = "training_data_size")
    private Integer trainingDataSize;
    
    @Column(name = "last_training_date")
    private LocalDateTime lastTrainingDate;
    
    // Drift Detection
    @Column(name = "data_drift_score", precision = 5, scale = 2)
    private BigDecimal dataDriftScore;
    
    @Column(name = "model_drift_score", precision = 5, scale = 2)
    private BigDecimal modelDriftScore;
    
    @Column(name = "drift_alert_triggered")
    private Boolean driftAlertTriggered = false;
    
    // Business Metrics
    @Column(name = "business_impact_score", precision = 5, scale = 2)
    private BigDecimal businessImpactScore;
    
    @Column(name = "cost_savings", precision = 12, scale = 2)
    private BigDecimal costSavings;
    
    @Column(name = "risk_reduction", precision = 5, scale = 2)
    private BigDecimal riskReduction;
    
    // Metadata
    @Column(name = "evaluation_date", nullable = false)
    private LocalDateTime evaluationDate;
    
    @Column(name = "evaluated_by", length = 100)
    private String evaluatedBy;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (evaluationDate == null) {
            evaluationDate = LocalDateTime.now();
        }
    }
    
    // Enums
    public enum ModelType {
        CLASSIFICATION,
        REGRESSION,
        TIME_SERIES,
        CLUSTERING,
        ANOMALY_DETECTION,
        DEEP_LEARNING,
        ENSEMBLE
    }
    
    // Default constructor
    public MLModelMetrics() {}
    
    // Builder pattern
    public static class Builder {
        private MLModelMetrics metrics = new MLModelMetrics();
        
        public Builder modelId(String modelId) {
            metrics.modelId = modelId;
            return this;
        }
        
        public Builder modelName(String modelName) {
            metrics.modelName = modelName;
            return this;
        }
        
        public Builder modelVersion(String modelVersion) {
            metrics.modelVersion = modelVersion;
            return this;
        }
        
        public Builder modelType(ModelType modelType) {
            metrics.modelType = modelType;
            return this;
        }
        
        public Builder accuracy(BigDecimal accuracy) {
            metrics.accuracy = accuracy;
            return this;
        }
        
        public Builder precision(BigDecimal precision) {
            metrics.precision = precision;
            return this;
        }
        
        public Builder recall(BigDecimal recall) {
            metrics.recall = recall;
            return this;
        }
        
        public Builder f1Score(BigDecimal f1Score) {
            metrics.f1Score = f1Score;
            return this;
        }
        
        public Builder auc(BigDecimal auc) {
            metrics.auc = auc;
            return this;
        }
        
        public Builder averageLatencyMs(Long averageLatencyMs) {
            metrics.averageLatencyMs = averageLatencyMs;
            return this;
        }
        
        public Builder throughputPerSecond(Long throughputPerSecond) {
            metrics.throughputPerSecond = throughputPerSecond;
            return this;
        }
        
        public Builder resourceUtilization(BigDecimal resourceUtilization) {
            metrics.resourceUtilization = resourceUtilization;
            return this;
        }
        
        public Builder dataQualityScore(BigDecimal dataQualityScore) {
            metrics.dataQualityScore = dataQualityScore;
            return this;
        }
        
        public Builder trainingDataSize(Integer trainingDataSize) {
            metrics.trainingDataSize = trainingDataSize;
            return this;
        }
        
        public Builder lastTrainingDate(LocalDateTime lastTrainingDate) {
            metrics.lastTrainingDate = lastTrainingDate;
            return this;
        }
        
        public Builder dataDriftScore(BigDecimal dataDriftScore) {
            metrics.dataDriftScore = dataDriftScore;
            return this;
        }
        
        public Builder modelDriftScore(BigDecimal modelDriftScore) {
            metrics.modelDriftScore = modelDriftScore;
            return this;
        }
        
        public Builder driftAlertTriggered(Boolean driftAlertTriggered) {
            metrics.driftAlertTriggered = driftAlertTriggered;
            return this;
        }
        
        public Builder businessImpactScore(BigDecimal businessImpactScore) {
            metrics.businessImpactScore = businessImpactScore;
            return this;
        }
        
        public Builder costSavings(BigDecimal costSavings) {
            metrics.costSavings = costSavings;
            return this;
        }
        
        public Builder riskReduction(BigDecimal riskReduction) {
            metrics.riskReduction = riskReduction;
            return this;
        }
        
        public Builder evaluationDate(LocalDateTime evaluationDate) {
            metrics.evaluationDate = evaluationDate;
            return this;
        }
        
        public Builder evaluatedBy(String evaluatedBy) {
            metrics.evaluatedBy = evaluatedBy;
            return this;
        }
        
        public MLModelMetrics build() {
            return metrics;
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
    
    public String getModelId() {
        return modelId;
    }
    
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }
    
    public String getModelName() {
        return modelName;
    }
    
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    
    public String getModelVersion() {
        return modelVersion;
    }
    
    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }
    
    public ModelType getModelType() {
        return modelType;
    }
    
    public void setModelType(ModelType modelType) {
        this.modelType = modelType;
    }
    
    public BigDecimal getAccuracy() {
        return accuracy;
    }
    
    public void setAccuracy(BigDecimal accuracy) {
        this.accuracy = accuracy;
    }
    
    public BigDecimal getPrecision() {
        return precision;
    }
    
    public void setPrecision(BigDecimal precision) {
        this.precision = precision;
    }
    
    public BigDecimal getRecall() {
        return recall;
    }
    
    public void setRecall(BigDecimal recall) {
        this.recall = recall;
    }
    
    public BigDecimal getF1Score() {
        return f1Score;
    }
    
    public void setF1Score(BigDecimal f1Score) {
        this.f1Score = f1Score;
    }
    
    public BigDecimal getAuc() {
        return auc;
    }
    
    public void setAuc(BigDecimal auc) {
        this.auc = auc;
    }
    
    public Long getAverageLatencyMs() {
        return averageLatencyMs;
    }
    
    public void setAverageLatencyMs(Long averageLatencyMs) {
        this.averageLatencyMs = averageLatencyMs;
    }
    
    public Long getThroughputPerSecond() {
        return throughputPerSecond;
    }
    
    public void setThroughputPerSecond(Long throughputPerSecond) {
        this.throughputPerSecond = throughputPerSecond;
    }
    
    public BigDecimal getResourceUtilization() {
        return resourceUtilization;
    }
    
    public void setResourceUtilization(BigDecimal resourceUtilization) {
        this.resourceUtilization = resourceUtilization;
    }
    
    public BigDecimal getDataQualityScore() {
        return dataQualityScore;
    }
    
    public void setDataQualityScore(BigDecimal dataQualityScore) {
        this.dataQualityScore = dataQualityScore;
    }
    
    public Integer getTrainingDataSize() {
        return trainingDataSize;
    }
    
    public void setTrainingDataSize(Integer trainingDataSize) {
        this.trainingDataSize = trainingDataSize;
    }
    
    public LocalDateTime getLastTrainingDate() {
        return lastTrainingDate;
    }
    
    public void setLastTrainingDate(LocalDateTime lastTrainingDate) {
        this.lastTrainingDate = lastTrainingDate;
    }
    
    public BigDecimal getDataDriftScore() {
        return dataDriftScore;
    }
    
    public void setDataDriftScore(BigDecimal dataDriftScore) {
        this.dataDriftScore = dataDriftScore;
    }
    
    public BigDecimal getModelDriftScore() {
        return modelDriftScore;
    }
    
    public void setModelDriftScore(BigDecimal modelDriftScore) {
        this.modelDriftScore = modelDriftScore;
    }
    
    public Boolean getDriftAlertTriggered() {
        return driftAlertTriggered;
    }
    
    public void setDriftAlertTriggered(Boolean driftAlertTriggered) {
        this.driftAlertTriggered = driftAlertTriggered;
    }
    
    public BigDecimal getBusinessImpactScore() {
        return businessImpactScore;
    }
    
    public void setBusinessImpactScore(BigDecimal businessImpactScore) {
        this.businessImpactScore = businessImpactScore;
    }
    
    public BigDecimal getCostSavings() {
        return costSavings;
    }
    
    public void setCostSavings(BigDecimal costSavings) {
        this.costSavings = costSavings;
    }
    
    public BigDecimal getRiskReduction() {
        return riskReduction;
    }
    
    public void setRiskReduction(BigDecimal riskReduction) {
        this.riskReduction = riskReduction;
    }
    
    public LocalDateTime getEvaluationDate() {
        return evaluationDate;
    }
    
    public void setEvaluationDate(LocalDateTime evaluationDate) {
        this.evaluationDate = evaluationDate;
    }
    
    public String getEvaluatedBy() {
        return evaluatedBy;
    }
    
    public void setEvaluatedBy(String evaluatedBy) {
        this.evaluatedBy = evaluatedBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
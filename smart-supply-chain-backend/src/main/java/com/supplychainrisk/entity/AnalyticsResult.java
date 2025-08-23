package com.supplychainrisk.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "analytics_results")
public class AnalyticsResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "analysis_type", nullable = false, length = 50)
    private String analysisType; // DEMAND_FORECAST, RISK_PREDICTION, OPTIMIZATION
    
    @Column(name = "supplier_id")
    private Long supplierId;
    
    @Column(name = "time_horizon_days")
    private Integer timeHorizonDays;
    
    @Column(name = "confidence_score", precision = 5, scale = 2)
    private BigDecimal confidenceScore; // 0-100
    
    @Column(name = "predictions", columnDefinition = "TEXT")
    private String predictions; // JSON string of prediction results
    
    @Column(name = "risk_scores", columnDefinition = "TEXT") 
    private String riskScores; // JSON string of risk score breakdown
    
    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations; // JSON string of recommendations
    
    @Column(name = "model_version", length = 20)
    private String modelVersion;
    
    @Column(name = "processing_time_ms")
    private Long processingTimeMs;
    
    @Column(name = "accuracy_score", precision = 5, scale = 2)
    private BigDecimal accuracyScore; // Back-tested accuracy if available
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private AnalyticsStatus status = AnalyticsStatus.ACTIVE;
    
    public enum AnalyticsStatus {
        ACTIVE, EXPIRED, SUPERSEDED
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Default constructor
    public AnalyticsResult() {}
    
    // Builder pattern constructor
    public AnalyticsResult(String analysisType, Long supplierId, Integer timeHorizonDays) {
        this.analysisType = analysisType;
        this.supplierId = supplierId;
        this.timeHorizonDays = timeHorizonDays;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAnalysisType() {
        return analysisType;
    }
    
    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }
    
    public Long getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }
    
    public Integer getTimeHorizonDays() {
        return timeHorizonDays;
    }
    
    public void setTimeHorizonDays(Integer timeHorizonDays) {
        this.timeHorizonDays = timeHorizonDays;
    }
    
    public BigDecimal getConfidenceScore() {
        return confidenceScore;
    }
    
    public void setConfidenceScore(BigDecimal confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
    
    public String getPredictions() {
        return predictions;
    }
    
    public void setPredictions(String predictions) {
        this.predictions = predictions;
    }
    
    public String getRiskScores() {
        return riskScores;
    }
    
    public void setRiskScores(String riskScores) {
        this.riskScores = riskScores;
    }
    
    public String getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }
    
    public String getModelVersion() {
        return modelVersion;
    }
    
    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }
    
    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }
    
    public void setProcessingTimeMs(Long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }
    
    public BigDecimal getAccuracyScore() {
        return accuracyScore;
    }
    
    public void setAccuracyScore(BigDecimal accuracyScore) {
        this.accuracyScore = accuracyScore;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public AnalyticsStatus getStatus() {
        return status;
    }
    
    public void setStatus(AnalyticsStatus status) {
        this.status = status;
    }
}
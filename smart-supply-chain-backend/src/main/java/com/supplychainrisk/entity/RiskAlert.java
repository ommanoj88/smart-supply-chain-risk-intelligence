package com.supplychainrisk.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_alerts")
public class RiskAlert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "alert_type", nullable = false, length = 50)
    private String alertType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Severity severity;
    
    @Column(nullable = false, length = 255)
    private String title;
    
    @Column(length = 2000)
    private String description;
    
    // Source information
    @Column(name = "source_type", length = 50)
    private String sourceType;
    
    @Column(name = "source_id")
    private Long sourceId;
    
    // Risk details
    @Column(name = "risk_score")
    private Integer riskScore;
    
    @Column(name = "impact_assessment", length = 2000)
    private String impactAssessment;
    
    @ElementCollection
    @CollectionTable(name = "risk_alert_recommendations", 
                     joinColumns = @JoinColumn(name = "risk_alert_id"))
    @Column(name = "recommendation")
    private java.util.List<String> recommendedActions;
    
    // Alert status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ACTIVE;
    
    @ManyToOne
    @JoinColumn(name = "acknowledged_by")
    private User acknowledgedBy;
    
    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;
    
    @ManyToOne
    @JoinColumn(name = "resolved_by")
    private User resolvedBy;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @Column(name = "resolution_notes", length = 2000)
    private String resolutionNotes;
    
    // Metadata stored as JSON
    @Column(columnDefinition = "jsonb")
    private String metadata;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum Severity {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    public enum Status {
        ACTIVE, ACKNOWLEDGED, RESOLVED, DISMISSED
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Default constructor
    public RiskAlert() {}
    
    // Constructor
    public RiskAlert(String alertType, Severity severity, String title, String description) {
        this.alertType = alertType;
        this.severity = severity;
        this.title = title;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAlertType() {
        return alertType;
    }
    
    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }
    
    public Severity getSeverity() {
        return severity;
    }
    
    public void setSeverity(Severity severity) {
        this.severity = severity;
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
    
    public String getSourceType() {
        return sourceType;
    }
    
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
    
    public Long getSourceId() {
        return sourceId;
    }
    
    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }
    
    public Integer getRiskScore() {
        return riskScore;
    }
    
    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }
    
    public String getImpactAssessment() {
        return impactAssessment;
    }
    
    public void setImpactAssessment(String impactAssessment) {
        this.impactAssessment = impactAssessment;
    }
    
    public java.util.List<String> getRecommendedActions() {
        return recommendedActions;
    }
    
    public void setRecommendedActions(java.util.List<String> recommendedActions) {
        this.recommendedActions = recommendedActions;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public User getAcknowledgedBy() {
        return acknowledgedBy;
    }
    
    public void setAcknowledgedBy(User acknowledgedBy) {
        this.acknowledgedBy = acknowledgedBy;
    }
    
    public LocalDateTime getAcknowledgedAt() {
        return acknowledgedAt;
    }
    
    public void setAcknowledgedAt(LocalDateTime acknowledgedAt) {
        this.acknowledgedAt = acknowledgedAt;
    }
    
    public User getResolvedBy() {
        return resolvedBy;
    }
    
    public void setResolvedBy(User resolvedBy) {
        this.resolvedBy = resolvedBy;
    }
    
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
    
    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
    
    public String getResolutionNotes() {
        return resolutionNotes;
    }
    
    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
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
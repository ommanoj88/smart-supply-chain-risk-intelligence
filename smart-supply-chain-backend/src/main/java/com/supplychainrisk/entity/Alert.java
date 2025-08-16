package com.supplychainrisk.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "alerts")
public class Alert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Alert Classification
    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false)
    private AlertType alertType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertSeverity severity;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertCategory category;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertStatus status = AlertStatus.NEW;
    
    // Alert Content
    @Column(nullable = false, length = 500)
    private String title;
    
    @Column(length = 2000)
    private String description;
    
    @Column(length = 1000)
    private String summary;
    
    // Source Information
    @Column(name = "source_system", length = 100)
    private String sourceSystem;
    
    @Column(name = "source_entity_type", length = 100)
    private String sourceEntityType;
    
    @Column(name = "source_entity_id")
    private String sourceEntityId;
    
    // Risk Information
    @Column(name = "risk_score", precision = 5, scale = 2)
    private Double riskScore;
    
    @Column(name = "risk_factors", length = 2000)
    private String riskFactors;
    
    @Column(name = "impact_score", precision = 5, scale = 2)
    private Double impactScore;
    
    // Thresholds and Conditions
    @Column(name = "trigger_condition", length = 1000)
    private String triggerCondition;
    
    @Column(name = "threshold_value")
    private String thresholdValue;
    
    @Column(name = "actual_value")
    private String actualValue;
    
    // Assignment and Ownership
    @Column(name = "assigned_to")
    private String assignedTo;
    
    @Column(name = "assigned_team")
    private String assignedTeam;
    
    @Column(name = "escalation_level")
    private String escalationLevel;
    
    // Lifecycle Tracking
    @Column(name = "detected_at")
    private LocalDateTime detectedAt;
    
    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @Column(name = "closed_at")
    private LocalDateTime closedAt;
    
    // Acknowledgment Information
    @Column(name = "acknowledged_by")
    private String acknowledgedBy;
    
    @Column(name = "acknowledgment_note", length = 1000)
    private String acknowledgmentNote;
    
    // Resolution Information
    @Column(name = "resolved_by")
    private String resolvedBy;
    
    @Column(name = "resolution_note", length = 1000)
    private String resolutionNote;
    
    @Column(name = "root_cause", length = 1000)
    private String rootCause;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "resolution_type")
    private ResolutionType resolutionType;
    
    // Related Entities
    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
    
    @ManyToOne
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;
    
    // Notifications
    @OneToMany(mappedBy = "sourceAlert", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications = new ArrayList<>();
    
    // Comments and Updates
    @OneToMany(mappedBy = "alert", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AlertComment> comments = new ArrayList<>();
    
    // Escalation Tracking
    @OneToMany(mappedBy = "alert", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AlertEscalation> escalations = new ArrayList<>();
    
    // Metadata
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    // Enums
    public enum AlertType {
        RISK_ALERT, PERFORMANCE_ALERT, SYSTEM_ALERT, COMPLIANCE_ALERT, 
        FINANCIAL_ALERT, OPERATIONAL_ALERT, SECURITY_ALERT
    }
    
    public enum AlertSeverity {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    public enum AlertCategory {
        SUPPLIER_RISK, SHIPMENT_DELAY, QUALITY_ISSUE, COMPLIANCE_VIOLATION,
        PERFORMANCE_DEGRADATION, SYSTEM_FAILURE, SECURITY_BREACH
    }
    
    public enum AlertStatus {
        NEW, ACKNOWLEDGED, IN_PROGRESS, RESOLVED, CLOSED, DISMISSED
    }
    
    public enum ResolutionType {
        FIXED, WORKAROUND, ACKNOWLEDGED, FALSE_POSITIVE, DUPLICATE
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (detectedAt == null) {
            detectedAt = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Alert() {}
    
    public Alert(AlertType alertType, AlertSeverity severity, AlertCategory category, String title, String description) {
        this.alertType = alertType;
        this.severity = severity;
        this.category = category;
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
    
    public AlertType getAlertType() {
        return alertType;
    }
    
    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }
    
    public AlertSeverity getSeverity() {
        return severity;
    }
    
    public void setSeverity(AlertSeverity severity) {
        this.severity = severity;
    }
    
    public AlertCategory getCategory() {
        return category;
    }
    
    public void setCategory(AlertCategory category) {
        this.category = category;
    }
    
    public AlertStatus getStatus() {
        return status;
    }
    
    public void setStatus(AlertStatus status) {
        this.status = status;
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
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public String getSourceSystem() {
        return sourceSystem;
    }
    
    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }
    
    public String getSourceEntityType() {
        return sourceEntityType;
    }
    
    public void setSourceEntityType(String sourceEntityType) {
        this.sourceEntityType = sourceEntityType;
    }
    
    public String getSourceEntityId() {
        return sourceEntityId;
    }
    
    public void setSourceEntityId(String sourceEntityId) {
        this.sourceEntityId = sourceEntityId;
    }
    
    public Double getRiskScore() {
        return riskScore;
    }
    
    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }
    
    public String getRiskFactors() {
        return riskFactors;
    }
    
    public void setRiskFactors(String riskFactors) {
        this.riskFactors = riskFactors;
    }
    
    public Double getImpactScore() {
        return impactScore;
    }
    
    public void setImpactScore(Double impactScore) {
        this.impactScore = impactScore;
    }
    
    public String getTriggerCondition() {
        return triggerCondition;
    }
    
    public void setTriggerCondition(String triggerCondition) {
        this.triggerCondition = triggerCondition;
    }
    
    public String getThresholdValue() {
        return thresholdValue;
    }
    
    public void setThresholdValue(String thresholdValue) {
        this.thresholdValue = thresholdValue;
    }
    
    public String getActualValue() {
        return actualValue;
    }
    
    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }
    
    public String getAssignedTo() {
        return assignedTo;
    }
    
    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
    
    public String getAssignedTeam() {
        return assignedTeam;
    }
    
    public void setAssignedTeam(String assignedTeam) {
        this.assignedTeam = assignedTeam;
    }
    
    public String getEscalationLevel() {
        return escalationLevel;
    }
    
    public void setEscalationLevel(String escalationLevel) {
        this.escalationLevel = escalationLevel;
    }
    
    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }
    
    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }
    
    public LocalDateTime getAcknowledgedAt() {
        return acknowledgedAt;
    }
    
    public void setAcknowledgedAt(LocalDateTime acknowledgedAt) {
        this.acknowledgedAt = acknowledgedAt;
    }
    
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
    
    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
    
    public LocalDateTime getClosedAt() {
        return closedAt;
    }
    
    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }
    
    public String getAcknowledgedBy() {
        return acknowledgedBy;
    }
    
    public void setAcknowledgedBy(String acknowledgedBy) {
        this.acknowledgedBy = acknowledgedBy;
    }
    
    public String getAcknowledgmentNote() {
        return acknowledgmentNote;
    }
    
    public void setAcknowledgmentNote(String acknowledgmentNote) {
        this.acknowledgmentNote = acknowledgmentNote;
    }
    
    public String getResolvedBy() {
        return resolvedBy;
    }
    
    public void setResolvedBy(String resolvedBy) {
        this.resolvedBy = resolvedBy;
    }
    
    public String getResolutionNote() {
        return resolutionNote;
    }
    
    public void setResolutionNote(String resolutionNote) {
        this.resolutionNote = resolutionNote;
    }
    
    public String getRootCause() {
        return rootCause;
    }
    
    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }
    
    public ResolutionType getResolutionType() {
        return resolutionType;
    }
    
    public void setResolutionType(ResolutionType resolutionType) {
        this.resolutionType = resolutionType;
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
    
    public List<Notification> getNotifications() {
        return notifications;
    }
    
    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
    
    public List<AlertComment> getComments() {
        return comments;
    }
    
    public void setComments(List<AlertComment> comments) {
        this.comments = comments;
    }
    
    public List<AlertEscalation> getEscalations() {
        return escalations;
    }
    
    public void setEscalations(List<AlertEscalation> escalations) {
        this.escalations = escalations;
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
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
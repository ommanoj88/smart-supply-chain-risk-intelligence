package com.supplychainrisk.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alert_escalations")
public class AlertEscalation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "alert_id", nullable = false)
    private Alert alert;
    
    @Column(name = "escalation_level", nullable = false)
    private Integer escalationLevel;
    
    @Column(name = "escalated_to", nullable = false)
    private String escalatedTo;
    
    @Column(name = "escalated_by")
    private String escalatedBy;
    
    @Column(name = "escalation_reason", length = 1000)
    private String escalationReason;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "escalation_status", nullable = false)
    private EscalationStatus status = EscalationStatus.PENDING;
    
    @Column(name = "escalated_at", nullable = false)
    private LocalDateTime escalatedAt;
    
    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @Column(name = "notes", length = 2000)
    private String notes;
    
    public enum EscalationStatus {
        PENDING, ACKNOWLEDGED, IN_PROGRESS, RESOLVED, CANCELLED
    }
    
    @PrePersist
    protected void onCreate() {
        if (escalatedAt == null) {
            escalatedAt = LocalDateTime.now();
        }
    }
    
    // Constructors
    public AlertEscalation() {}
    
    public AlertEscalation(Alert alert, Integer escalationLevel, String escalatedTo, String escalationReason) {
        this.alert = alert;
        this.escalationLevel = escalationLevel;
        this.escalatedTo = escalatedTo;
        this.escalationReason = escalationReason;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Alert getAlert() {
        return alert;
    }
    
    public void setAlert(Alert alert) {
        this.alert = alert;
    }
    
    public Integer getEscalationLevel() {
        return escalationLevel;
    }
    
    public void setEscalationLevel(Integer escalationLevel) {
        this.escalationLevel = escalationLevel;
    }
    
    public String getEscalatedTo() {
        return escalatedTo;
    }
    
    public void setEscalatedTo(String escalatedTo) {
        this.escalatedTo = escalatedTo;
    }
    
    public String getEscalatedBy() {
        return escalatedBy;
    }
    
    public void setEscalatedBy(String escalatedBy) {
        this.escalatedBy = escalatedBy;
    }
    
    public String getEscalationReason() {
        return escalationReason;
    }
    
    public void setEscalationReason(String escalationReason) {
        this.escalationReason = escalationReason;
    }
    
    public EscalationStatus getStatus() {
        return status;
    }
    
    public void setStatus(EscalationStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getEscalatedAt() {
        return escalatedAt;
    }
    
    public void setEscalatedAt(LocalDateTime escalatedAt) {
        this.escalatedAt = escalatedAt;
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
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
package com.smartsupplychain.riskintelligence.model;

import com.smartsupplychain.riskintelligence.enums.AlertSeverity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Message is required")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Severity is required")
    private AlertSeverity severity;

    @Column(name = "is_acknowledged")
    private Boolean isAcknowledged = false;

    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;

    @Column(name = "reference_id")
    private String referenceId; // Can reference shipment, supplier, etc.

    @Column(name = "reference_type")
    private String referenceType; // Type of reference (SHIPMENT, SUPPLIER, etc.)

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public Alert() {}

    public Alert(User user, String message, AlertSeverity severity) {
        this.user = user;
        this.message = message;
        this.severity = severity;
    }

    public Alert(User user, String message, AlertSeverity severity, String referenceId, String referenceType) {
        this.user = user;
        this.message = message;
        this.severity = severity;
        this.referenceId = referenceId;
        this.referenceType = referenceType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AlertSeverity severity) {
        this.severity = severity;
    }

    public Boolean getIsAcknowledged() {
        return isAcknowledged;
    }

    public void setIsAcknowledged(Boolean isAcknowledged) {
        this.isAcknowledged = isAcknowledged;
    }

    public LocalDateTime getAcknowledgedAt() {
        return acknowledgedAt;
    }

    public void setAcknowledgedAt(LocalDateTime acknowledgedAt) {
        this.acknowledgedAt = acknowledgedAt;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
package com.supplychainrisk.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "escalation_rules")
public class EscalationRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "alert_configuration_id", nullable = false)
    private AlertConfiguration alertConfiguration;
    
    @Column(name = "escalation_level", nullable = false)
    private Integer escalationLevel;
    
    @Column(name = "escalation_delay_minutes", nullable = false)
    private Integer escalationDelayMinutes;
    
    @Column(name = "escalate_to", nullable = false)
    private String escalateTo;
    
    @Column(name = "escalation_type", length = 50)
    private String escalationType; // USER, TEAM, ROLE
    
    @Column(name = "escalation_condition", length = 1000)
    private String escalationCondition;
    
    @Column(name = "notification_message", length = 1000)
    private String notificationMessage;
    
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public EscalationRule() {}
    
    public EscalationRule(AlertConfiguration alertConfiguration, Integer escalationLevel, 
                         Integer escalationDelayMinutes, String escalateTo) {
        this.alertConfiguration = alertConfiguration;
        this.escalationLevel = escalationLevel;
        this.escalationDelayMinutes = escalationDelayMinutes;
        this.escalateTo = escalateTo;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public AlertConfiguration getAlertConfiguration() {
        return alertConfiguration;
    }
    
    public void setAlertConfiguration(AlertConfiguration alertConfiguration) {
        this.alertConfiguration = alertConfiguration;
    }
    
    public Integer getEscalationLevel() {
        return escalationLevel;
    }
    
    public void setEscalationLevel(Integer escalationLevel) {
        this.escalationLevel = escalationLevel;
    }
    
    public Integer getEscalationDelayMinutes() {
        return escalationDelayMinutes;
    }
    
    public void setEscalationDelayMinutes(Integer escalationDelayMinutes) {
        this.escalationDelayMinutes = escalationDelayMinutes;
    }
    
    public String getEscalateTo() {
        return escalateTo;
    }
    
    public void setEscalateTo(String escalateTo) {
        this.escalateTo = escalateTo;
    }
    
    public String getEscalationType() {
        return escalationType;
    }
    
    public void setEscalationType(String escalationType) {
        this.escalationType = escalationType;
    }
    
    public String getEscalationCondition() {
        return escalationCondition;
    }
    
    public void setEscalationCondition(String escalationCondition) {
        this.escalationCondition = escalationCondition;
    }
    
    public String getNotificationMessage() {
        return notificationMessage;
    }
    
    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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
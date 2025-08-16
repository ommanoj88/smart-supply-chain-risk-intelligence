package com.supplychainrisk.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "alert_configurations")
public class AlertConfiguration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Configuration Details
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private Boolean enabled = true;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false)
    private Alert.AlertType alertType;
    
    // Threshold Configuration
    @ElementCollection
    @CollectionTable(name = "alert_thresholds", joinColumns = @JoinColumn(name = "alert_config_id"))
    @MapKeyColumn(name = "threshold_key")
    @Column(name = "threshold_value")
    private Map<String, String> thresholds = new HashMap<>();
    
    // Severity Rules
    @Column(name = "severity_rules", columnDefinition = "TEXT")
    private String severityRules; // JSON format
    
    // Target Entities
    @Column(name = "entity_type", length = 100)
    private String entityType;
    
    @Column(name = "entity_filter", length = 1000)
    private String entityFilter;
    
    // Notification Settings
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "alert_notification_channels", joinColumns = @JoinColumn(name = "alert_config_id"))
    @Column(name = "channel")
    private Set<Notification.NotificationChannel> notificationChannels = new HashSet<>();
    
    // Recipients Configuration
    @ElementCollection
    @CollectionTable(name = "alert_recipients", joinColumns = @JoinColumn(name = "alert_config_id"))
    @Column(name = "recipient")
    private Set<String> recipients = new HashSet<>();
    
    // Escalation Configuration
    @OneToMany(mappedBy = "alertConfiguration", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EscalationRule> escalationRules = new ArrayList<>();
    
    // Scheduling
    @Column(name = "schedule")
    private String schedule; // Cron expression
    
    @Column(name = "business_hours_only")
    private Boolean businessHoursOnly = false;
    
    @Column(length = 50)
    private String timezone = "UTC";
    
    // Suppression Rules
    @Column(name = "suppression_rules", columnDefinition = "TEXT")
    private String suppressionRules; // JSON format
    
    // Template Configuration
    @Column(name = "email_template_id")
    private String emailTemplateId;
    
    @Column(name = "slack_template_id")
    private String slackTemplateId;
    
    @Column(name = "sms_template_id")
    private String smsTemplateId;
    
    // Metadata
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
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
    public AlertConfiguration() {}
    
    public AlertConfiguration(String name, Alert.AlertType alertType, String description) {
        this.name = name;
        this.alertType = alertType;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public Alert.AlertType getAlertType() {
        return alertType;
    }
    
    public void setAlertType(Alert.AlertType alertType) {
        this.alertType = alertType;
    }
    
    public Map<String, String> getThresholds() {
        return thresholds;
    }
    
    public void setThresholds(Map<String, String> thresholds) {
        this.thresholds = thresholds;
    }
    
    public String getSeverityRules() {
        return severityRules;
    }
    
    public void setSeverityRules(String severityRules) {
        this.severityRules = severityRules;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    
    public String getEntityFilter() {
        return entityFilter;
    }
    
    public void setEntityFilter(String entityFilter) {
        this.entityFilter = entityFilter;
    }
    
    public Set<Notification.NotificationChannel> getNotificationChannels() {
        return notificationChannels;
    }
    
    public void setNotificationChannels(Set<Notification.NotificationChannel> notificationChannels) {
        this.notificationChannels = notificationChannels;
    }
    
    public Set<String> getRecipients() {
        return recipients;
    }
    
    public void setRecipients(Set<String> recipients) {
        this.recipients = recipients;
    }
    
    public List<EscalationRule> getEscalationRules() {
        return escalationRules;
    }
    
    public void setEscalationRules(List<EscalationRule> escalationRules) {
        this.escalationRules = escalationRules;
    }
    
    public String getSchedule() {
        return schedule;
    }
    
    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
    
    public Boolean getBusinessHoursOnly() {
        return businessHoursOnly;
    }
    
    public void setBusinessHoursOnly(Boolean businessHoursOnly) {
        this.businessHoursOnly = businessHoursOnly;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    
    public String getSuppressionRules() {
        return suppressionRules;
    }
    
    public void setSuppressionRules(String suppressionRules) {
        this.suppressionRules = suppressionRules;
    }
    
    public String getEmailTemplateId() {
        return emailTemplateId;
    }
    
    public void setEmailTemplateId(String emailTemplateId) {
        this.emailTemplateId = emailTemplateId;
    }
    
    public String getSlackTemplateId() {
        return slackTemplateId;
    }
    
    public void setSlackTemplateId(String slackTemplateId) {
        this.slackTemplateId = slackTemplateId;
    }
    
    public String getSmsTemplateId() {
        return smsTemplateId;
    }
    
    public void setSmsTemplateId(String smsTemplateId) {
        this.smsTemplateId = smsTemplateId;
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
    
    public String getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
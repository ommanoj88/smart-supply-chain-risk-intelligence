package com.supplychainrisk.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "channel_id")
    private Long channelId;
    
    @Column(name = "template_id")
    private Long templateId;
    
    @Column(nullable = false, length = 50)
    private String category;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Priority priority = Priority.MEDIUM;
    
    @Column(length = 500)
    private String subject;
    
    @Column(nullable = false, length = 5000)
    private String content;
    
    // Enhanced Content and Formatting
    @Column(name = "html_content", columnDefinition = "TEXT")
    private String htmlContent;
    
    @Column(name = "description", length = 2000)
    private String description;
    
    // Notification Type and Severity
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private NotificationType type;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private NotificationSeverity severity;
    
    // Delivery Information
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status status = Status.PENDING;
    
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    @Column(name = "failed_at")
    private LocalDateTime failedAt;
    
    @Column(name = "failure_reason", length = 1000)
    private String failureReason;
    
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    @Column(name = "max_retries")
    private Integer maxRetries = 3;
    
    // Recipients
    @ElementCollection
    @CollectionTable(name = "notification_recipients", joinColumns = @JoinColumn(name = "notification_id"))
    @Column(name = "recipient")
    private Set<String> recipients = new HashSet<>();
    
    // Channels
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "notification_channels", joinColumns = @JoinColumn(name = "notification_id"))
    @Column(name = "channel")
    private Set<NotificationChannel> channels = new HashSet<>();
    
    // Alert Information
    @ManyToOne
    @JoinColumn(name = "alert_id")
    private Alert sourceAlert;
    
    // Context Data
    @Column(name = "context_data", columnDefinition = "TEXT")
    private String contextData; // JSON format
    
    // Delivery Tracking
    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NotificationDelivery> deliveries = new ArrayList<>();
    
    // Interactions
    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NotificationInteraction> interactions = new ArrayList<>();
    
    // Scheduling
    @Column(name = "scheduled_for")
    private LocalDateTime scheduledFor;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    // Metadata stored as JSON
    @Column(columnDefinition = "jsonb")
    private String metadata;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    // Enums
    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }
    
    public enum Status {
        PENDING, SENT, DELIVERED, FAILED, RETRYING
    }
    
    public enum NotificationType {
        ALERT, REMINDER, INFO, WARNING, ERROR, SUCCESS
    }
    
    public enum NotificationSeverity {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    public enum NotificationChannel {
        EMAIL, SLACK, PUSH, SMS, VOICE, IN_APP
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
    public Notification() {}
    
    // Constructor
    public Notification(User user, String category, String subject, String content, Priority priority) {
        this.user = user;
        this.category = category;
        this.subject = subject;
        this.content = content;
        this.priority = priority;
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
    
    public Long getChannelId() {
        return channelId;
    }
    
    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }
    
    public Long getTemplateId() {
        return templateId;
    }
    
    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Priority getPriority() {
        return priority;
    }
    
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getHtmlContent() {
        return htmlContent;
    }
    
    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public NotificationSeverity getSeverity() {
        return severity;
    }
    
    public void setSeverity(NotificationSeverity severity) {
        this.severity = severity;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }
    
    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
    
    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }
    
    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
    
    public LocalDateTime getReadAt() {
        return readAt;
    }
    
    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
    
    public LocalDateTime getFailedAt() {
        return failedAt;
    }
    
    public void setFailedAt(LocalDateTime failedAt) {
        this.failedAt = failedAt;
    }
    
    public String getFailureReason() {
        return failureReason;
    }
    
    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
    
    public Integer getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
    
    public Integer getMaxRetries() {
        return maxRetries;
    }
    
    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }
    
    public Set<String> getRecipients() {
        return recipients;
    }
    
    public void setRecipients(Set<String> recipients) {
        this.recipients = recipients;
    }
    
    public Set<NotificationChannel> getChannels() {
        return channels;
    }
    
    public void setChannels(Set<NotificationChannel> channels) {
        this.channels = channels;
    }
    
    public Alert getSourceAlert() {
        return sourceAlert;
    }
    
    public void setSourceAlert(Alert sourceAlert) {
        this.sourceAlert = sourceAlert;
    }
    
    public String getContextData() {
        return contextData;
    }
    
    public void setContextData(String contextData) {
        this.contextData = contextData;
    }
    
    public List<NotificationDelivery> getDeliveries() {
        return deliveries;
    }
    
    public void setDeliveries(List<NotificationDelivery> deliveries) {
        this.deliveries = deliveries;
    }
    
    public List<NotificationInteraction> getInteractions() {
        return interactions;
    }
    
    public void setInteractions(List<NotificationInteraction> interactions) {
        this.interactions = interactions;
    }
    
    public LocalDateTime getScheduledFor() {
        return scheduledFor;
    }
    
    public void setScheduledFor(LocalDateTime scheduledFor) {
        this.scheduledFor = scheduledFor;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
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
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
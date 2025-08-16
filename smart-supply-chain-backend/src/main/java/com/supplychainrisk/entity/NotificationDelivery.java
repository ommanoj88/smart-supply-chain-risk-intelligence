package com.supplychainrisk.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_deliveries")
public class NotificationDelivery {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private Notification.NotificationChannel channel;
    
    @Column(name = "recipient", nullable = false)
    private String recipient;
    
    @Column(name = "external_message_id")
    private String externalMessageId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false)
    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;
    
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
    
    @Column(name = "delivery_metadata", columnDefinition = "TEXT")
    private String deliveryMetadata; // JSON format
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum DeliveryStatus {
        PENDING, SENT, DELIVERED, READ, FAILED, BOUNCED, REJECTED
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
    
    // Constructors
    public NotificationDelivery() {}
    
    public NotificationDelivery(Notification notification, Notification.NotificationChannel channel, String recipient) {
        this.notification = notification;
        this.channel = channel;
        this.recipient = recipient;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Notification getNotification() {
        return notification;
    }
    
    public void setNotification(Notification notification) {
        this.notification = notification;
    }
    
    public Notification.NotificationChannel getChannel() {
        return channel;
    }
    
    public void setChannel(Notification.NotificationChannel channel) {
        this.channel = channel;
    }
    
    public String getRecipient() {
        return recipient;
    }
    
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    public String getExternalMessageId() {
        return externalMessageId;
    }
    
    public void setExternalMessageId(String externalMessageId) {
        this.externalMessageId = externalMessageId;
    }
    
    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }
    
    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
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
    
    public String getDeliveryMetadata() {
        return deliveryMetadata;
    }
    
    public void setDeliveryMetadata(String deliveryMetadata) {
        this.deliveryMetadata = deliveryMetadata;
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
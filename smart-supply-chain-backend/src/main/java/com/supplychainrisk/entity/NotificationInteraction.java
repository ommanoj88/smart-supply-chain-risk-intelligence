package com.supplychainrisk.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_interactions")
public class NotificationInteraction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "interaction_type", nullable = false)
    private InteractionType interactionType;
    
    @Column(name = "user_id")
    private String userId;
    
    @Column(name = "interaction_data", columnDefinition = "TEXT")
    private String interactionData; // JSON format
    
    @Column(name = "interaction_timestamp", nullable = false)
    private LocalDateTime interactionTimestamp;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    @Column(name = "device_info", length = 200)
    private String deviceInfo;
    
    public enum InteractionType {
        OPENED, CLICKED, ACKNOWLEDGED, DISMISSED, REPLIED
    }
    
    @PrePersist
    protected void onCreate() {
        if (interactionTimestamp == null) {
            interactionTimestamp = LocalDateTime.now();
        }
    }
    
    // Constructors
    public NotificationInteraction() {}
    
    public NotificationInteraction(Notification notification, InteractionType interactionType, String userId) {
        this.notification = notification;
        this.interactionType = interactionType;
        this.userId = userId;
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
    
    public InteractionType getInteractionType() {
        return interactionType;
    }
    
    public void setInteractionType(InteractionType interactionType) {
        this.interactionType = interactionType;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getInteractionData() {
        return interactionData;
    }
    
    public void setInteractionData(String interactionData) {
        this.interactionData = interactionData;
    }
    
    public LocalDateTime getInteractionTimestamp() {
        return interactionTimestamp;
    }
    
    public void setInteractionTimestamp(LocalDateTime interactionTimestamp) {
        this.interactionTimestamp = interactionTimestamp;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public String getDeviceInfo() {
        return deviceInfo;
    }
    
    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
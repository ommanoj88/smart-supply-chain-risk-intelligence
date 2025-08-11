package com.supplychainrisk.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "carriers")
public class Carrier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    @NotBlank(message = "Carrier name is required")
    private String name;

    @Column(nullable = false, length = 20, unique = true)
    @NotBlank(message = "Carrier code is required")
    private String code;

    // API Configuration
    @Column(name = "api_endpoint")
    private String apiEndpoint;

    @Column(name = "api_key_encrypted", columnDefinition = "TEXT")
    private String apiKeyEncrypted;

    @Column(name = "webhook_url")
    private String webhookUrl;

    // Service capabilities
    @ElementCollection
    @Column(name = "services_offered")
    private List<String> servicesOffered;

    @ElementCollection
    @Column(name = "countries_supported")
    private List<String> countriesSupported;

    @Column(name = "tracking_url_template", length = 500)
    private String trackingUrlTemplate;

    // Performance metrics
    @Column(name = "avg_delivery_time_days", precision = 5, scale = 2)
    private BigDecimal avgDeliveryTimeDays;

    @Column(name = "on_time_percentage", precision = 5, scale = 2)
    private BigDecimal onTimePercentage;

    @Column(name = "reliability_score")
    private Integer reliabilityScore = 0;

    // Status
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;

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

    // Default constructor
    public Carrier() {}

    // Constructor with required fields
    public Carrier(String name, String code) {
        this.name = name;
        this.code = code;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public String getApiKeyEncrypted() {
        return apiKeyEncrypted;
    }

    public void setApiKeyEncrypted(String apiKeyEncrypted) {
        this.apiKeyEncrypted = apiKeyEncrypted;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public List<String> getServicesOffered() {
        return servicesOffered;
    }

    public void setServicesOffered(List<String> servicesOffered) {
        this.servicesOffered = servicesOffered;
    }

    public List<String> getCountriesSupported() {
        return countriesSupported;
    }

    public void setCountriesSupported(List<String> countriesSupported) {
        this.countriesSupported = countriesSupported;
    }

    public String getTrackingUrlTemplate() {
        return trackingUrlTemplate;
    }

    public void setTrackingUrlTemplate(String trackingUrlTemplate) {
        this.trackingUrlTemplate = trackingUrlTemplate;
    }

    public BigDecimal getAvgDeliveryTimeDays() {
        return avgDeliveryTimeDays;
    }

    public void setAvgDeliveryTimeDays(BigDecimal avgDeliveryTimeDays) {
        this.avgDeliveryTimeDays = avgDeliveryTimeDays;
    }

    public BigDecimal getOnTimePercentage() {
        return onTimePercentage;
    }

    public void setOnTimePercentage(BigDecimal onTimePercentage) {
        this.onTimePercentage = onTimePercentage;
    }

    public Integer getReliabilityScore() {
        return reliabilityScore;
    }

    public void setReliabilityScore(Integer reliabilityScore) {
        this.reliabilityScore = reliabilityScore;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getLastSyncAt() {
        return lastSyncAt;
    }

    public void setLastSyncAt(LocalDateTime lastSyncAt) {
        this.lastSyncAt = lastSyncAt;
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
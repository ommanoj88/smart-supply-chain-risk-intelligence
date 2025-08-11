package com.supplychainrisk.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CarrierDTO {

    private Long id;

    @NotBlank(message = "Carrier name is required")
    private String name;

    @NotBlank(message = "Carrier code is required")
    private String code;

    // API Configuration
    private String apiEndpoint;
    private String webhookUrl;

    // Service capabilities
    private List<String> servicesOffered;
    private List<String> countriesSupported;
    private String trackingUrlTemplate;

    // Performance metrics
    private BigDecimal avgDeliveryTimeDays;
    private BigDecimal onTimePercentage;
    private Integer reliabilityScore;

    // Status
    private Boolean isActive;
    private LocalDateTime lastSyncAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public CarrierDTO() {}

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
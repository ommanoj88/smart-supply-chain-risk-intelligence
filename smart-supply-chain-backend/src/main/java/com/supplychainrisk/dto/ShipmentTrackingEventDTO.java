package com.supplychainrisk.dto;

import com.supplychainrisk.entity.ShipmentTrackingEvent.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ShipmentTrackingEventDTO {

    private Long id;
    private Long shipmentId;

    @NotBlank(message = "Event code is required")
    private String eventCode;

    @NotBlank(message = "Event description is required")
    private String eventDescription;

    @NotNull(message = "Event timestamp is required")
    private LocalDateTime eventTimestamp;

    // Location Information
    private String locationName;
    private String locationCity;
    private String locationState;
    private String locationCountry;
    private BigDecimal latitude;
    private BigDecimal longitude;

    // Event Details
    private EventType eventType;
    private Boolean isException;
    private String exceptionReason;

    // Carrier Data
    private String carrierEventCode;
    private String carrierRawData;

    private LocalDateTime createdAt;

    // Default constructor
    public ShipmentTrackingEventDTO() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Long shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(LocalDateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public void setLocationCity(String locationCity) {
        this.locationCity = locationCity;
    }

    public String getLocationState() {
        return locationState;
    }

    public void setLocationState(String locationState) {
        this.locationState = locationState;
    }

    public String getLocationCountry() {
        return locationCountry;
    }

    public void setLocationCountry(String locationCountry) {
        this.locationCountry = locationCountry;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Boolean getIsException() {
        return isException;
    }

    public void setIsException(Boolean isException) {
        this.isException = isException;
    }

    public String getExceptionReason() {
        return exceptionReason;
    }

    public void setExceptionReason(String exceptionReason) {
        this.exceptionReason = exceptionReason;
    }

    public String getCarrierEventCode() {
        return carrierEventCode;
    }

    public void setCarrierEventCode(String carrierEventCode) {
        this.carrierEventCode = carrierEventCode;
    }

    public String getCarrierRawData() {
        return carrierRawData;
    }

    public void setCarrierRawData(String carrierRawData) {
        this.carrierRawData = carrierRawData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
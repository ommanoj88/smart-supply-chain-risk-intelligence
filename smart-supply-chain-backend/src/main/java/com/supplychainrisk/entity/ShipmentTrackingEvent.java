package com.supplychainrisk.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipment_tracking_events")
public class ShipmentTrackingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shipment_id", nullable = false)
    @NotNull(message = "Shipment is required")
    private Shipment shipment;

    @Column(name = "event_code", nullable = false, length = 50)
    @NotBlank(message = "Event code is required")
    private String eventCode;

    @Column(name = "event_description", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Event description is required")
    private String eventDescription;

    @Column(name = "event_timestamp", nullable = false)
    @NotNull(message = "Event timestamp is required")
    private LocalDateTime eventTimestamp;

    // Location Information
    @Column(name = "location_name")
    private String locationName;

    @Column(name = "location_city", length = 100)
    private String locationCity;

    @Column(name = "location_state", length = 100)
    private String locationState;

    @Column(name = "location_country", length = 100)
    private String locationCountry;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    // Event Details
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", length = 50)
    private EventType eventType;

    @Column(name = "is_exception")
    private Boolean isException = false;

    @Column(name = "exception_reason", columnDefinition = "TEXT")
    private String exceptionReason;

    // Carrier Data
    @Column(name = "carrier_event_code", length = 50)
    private String carrierEventCode;

    @Column(name = "carrier_raw_data", columnDefinition = "jsonb")
    private String carrierRawData;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Enums
    public enum EventType {
        PICKUP, TRANSIT, DELIVERY, EXCEPTION
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Default constructor
    public ShipmentTrackingEvent() {}

    // Constructor with required fields
    public ShipmentTrackingEvent(Shipment shipment, String eventCode, String eventDescription, LocalDateTime eventTimestamp) {
        this.shipment = shipment;
        this.eventCode = eventCode;
        this.eventDescription = eventDescription;
        this.eventTimestamp = eventTimestamp;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
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
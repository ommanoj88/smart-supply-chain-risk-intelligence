package com.supplychainrisk.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tracking_number", unique = true, nullable = false, length = 100)
    @NotBlank(message = "Tracking number is required")
    private String trackingNumber;

    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    // Shipment Details
    @Enumerated(EnumType.STRING)
    @Column(name = "shipment_type", length = 50)
    private ShipmentType shipmentType = ShipmentType.STANDARD;

    @Column(name = "service_level", length = 50)
    private String serviceLevel;

    @Column(name = "weight_kg", precision = 10, scale = 3)
    private BigDecimal weightKg;

    @Column(name = "dimensions_length_cm", precision = 8, scale = 2)
    private BigDecimal dimensionsLengthCm;

    @Column(name = "dimensions_width_cm", precision = 8, scale = 2)
    private BigDecimal dimensionsWidthCm;

    @Column(name = "dimensions_height_cm", precision = 8, scale = 2)
    private BigDecimal dimensionsHeightCm;

    @Column(name = "declared_value", precision = 15, scale = 2)
    private BigDecimal declaredValue;

    @Column(length = 3)
    private String currency = "USD";

    // Origin Information
    @Column(name = "origin_name")
    private String originName;

    @Column(name = "origin_address", columnDefinition = "TEXT")
    private String originAddress;

    @Column(name = "origin_city", length = 100)
    private String originCity;

    @Column(name = "origin_state", length = 100)
    private String originState;

    @Column(name = "origin_country", length = 100)
    private String originCountry;

    @Column(name = "origin_postal_code", length = 20)
    private String originPostalCode;

    @Column(name = "origin_latitude", precision = 10, scale = 8)
    private BigDecimal originLatitude;

    @Column(name = "origin_longitude", precision = 11, scale = 8)
    private BigDecimal originLongitude;

    // Destination Information
    @Column(name = "destination_name")
    private String destinationName;

    @Column(name = "destination_address", columnDefinition = "TEXT")
    private String destinationAddress;

    @Column(name = "destination_city", length = 100)
    private String destinationCity;

    @Column(name = "destination_state", length = 100)
    private String destinationState;

    @Column(name = "destination_country", length = 100)
    private String destinationCountry;

    @Column(name = "destination_postal_code", length = 20)
    private String destinationPostalCode;

    @Column(name = "destination_latitude", precision = 10, scale = 8)
    private BigDecimal destinationLatitude;

    @Column(name = "destination_longitude", precision = 11, scale = 8)
    private BigDecimal destinationLongitude;

    // Carrier Information
    @Column(name = "carrier_name", nullable = false, length = 100)
    @NotBlank(message = "Carrier name is required")
    private String carrierName;

    @Column(name = "carrier_service_code", length = 50)
    private String carrierServiceCode;

    @Column(name = "carrier_tracking_url", columnDefinition = "TEXT")
    private String carrierTrackingUrl;

    // Status and Timing
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ShipmentStatus status = ShipmentStatus.CREATED;

    @Column(length = 100)
    private String substatus;

    @Column(name = "ship_date")
    private LocalDateTime shipDate;

    @Column(name = "estimated_delivery_date")
    private LocalDateTime estimatedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private LocalDateTime actualDeliveryDate;

    @Column(name = "transit_days")
    private Integer transitDays;

    // Risk and Performance
    @Column(name = "risk_score")
    @Min(value = 0, message = "Risk score cannot be negative")
    @Max(value = 100, message = "Risk score cannot exceed 100")
    private Integer riskScore = 0;

    @Column(name = "delay_risk_probability", precision = 5, scale = 2)
    private BigDecimal delayRiskProbability = BigDecimal.ZERO;

    @Column(name = "predicted_delay_hours")
    private Integer predictedDelayHours = 0;

    @Column(name = "on_time_performance")
    private Boolean onTimePerformance;

    // Cost and Billing
    @Column(name = "shipping_cost", precision = 10, scale = 2)
    private BigDecimal shippingCost;

    @Column(name = "fuel_surcharge", precision = 10, scale = 2)
    private BigDecimal fuelSurcharge;

    @Column(name = "total_cost", precision = 10, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "billed_weight_kg", precision = 10, scale = 3)
    private BigDecimal billedWeightKg;

    // Environmental Impact
    @Column(name = "carbon_footprint_kg", precision = 10, scale = 3)
    private BigDecimal carbonFootprintKg;

    // Audit Fields
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ShipmentTrackingEvent> trackingEvents;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ShipmentItem> items;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ShipmentDocument> documents;

    // Enums
    public enum ShipmentType {
        STANDARD, EXPRESS, FREIGHT
    }

    public enum ShipmentStatus {
        CREATED, PICKED_UP, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, EXCEPTION
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
    public Shipment() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public ShipmentType getShipmentType() {
        return shipmentType;
    }

    public void setShipmentType(ShipmentType shipmentType) {
        this.shipmentType = shipmentType;
    }

    public String getServiceLevel() {
        return serviceLevel;
    }

    public void setServiceLevel(String serviceLevel) {
        this.serviceLevel = serviceLevel;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public BigDecimal getDimensionsLengthCm() {
        return dimensionsLengthCm;
    }

    public void setDimensionsLengthCm(BigDecimal dimensionsLengthCm) {
        this.dimensionsLengthCm = dimensionsLengthCm;
    }

    public BigDecimal getDimensionsWidthCm() {
        return dimensionsWidthCm;
    }

    public void setDimensionsWidthCm(BigDecimal dimensionsWidthCm) {
        this.dimensionsWidthCm = dimensionsWidthCm;
    }

    public BigDecimal getDimensionsHeightCm() {
        return dimensionsHeightCm;
    }

    public void setDimensionsHeightCm(BigDecimal dimensionsHeightCm) {
        this.dimensionsHeightCm = dimensionsHeightCm;
    }

    public BigDecimal getDeclaredValue() {
        return declaredValue;
    }

    public void setDeclaredValue(BigDecimal declaredValue) {
        this.declaredValue = declaredValue;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getOriginState() {
        return originState;
    }

    public void setOriginState(String originState) {
        this.originState = originState;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    public String getOriginPostalCode() {
        return originPostalCode;
    }

    public void setOriginPostalCode(String originPostalCode) {
        this.originPostalCode = originPostalCode;
    }

    public BigDecimal getOriginLatitude() {
        return originLatitude;
    }

    public void setOriginLatitude(BigDecimal originLatitude) {
        this.originLatitude = originLatitude;
    }

    public BigDecimal getOriginLongitude() {
        return originLongitude;
    }

    public void setOriginLongitude(BigDecimal originLongitude) {
        this.originLongitude = originLongitude;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public String getDestinationState() {
        return destinationState;
    }

    public void setDestinationState(String destinationState) {
        this.destinationState = destinationState;
    }

    public String getDestinationCountry() {
        return destinationCountry;
    }

    public void setDestinationCountry(String destinationCountry) {
        this.destinationCountry = destinationCountry;
    }

    public String getDestinationPostalCode() {
        return destinationPostalCode;
    }

    public void setDestinationPostalCode(String destinationPostalCode) {
        this.destinationPostalCode = destinationPostalCode;
    }

    public BigDecimal getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(BigDecimal destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public BigDecimal getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(BigDecimal destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getCarrierServiceCode() {
        return carrierServiceCode;
    }

    public void setCarrierServiceCode(String carrierServiceCode) {
        this.carrierServiceCode = carrierServiceCode;
    }

    public String getCarrierTrackingUrl() {
        return carrierTrackingUrl;
    }

    public void setCarrierTrackingUrl(String carrierTrackingUrl) {
        this.carrierTrackingUrl = carrierTrackingUrl;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public String getSubstatus() {
        return substatus;
    }

    public void setSubstatus(String substatus) {
        this.substatus = substatus;
    }

    public LocalDateTime getShipDate() {
        return shipDate;
    }

    public void setShipDate(LocalDateTime shipDate) {
        this.shipDate = shipDate;
    }

    public LocalDateTime getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public LocalDateTime getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(LocalDateTime actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public Integer getTransitDays() {
        return transitDays;
    }

    public void setTransitDays(Integer transitDays) {
        this.transitDays = transitDays;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public BigDecimal getDelayRiskProbability() {
        return delayRiskProbability;
    }

    public void setDelayRiskProbability(BigDecimal delayRiskProbability) {
        this.delayRiskProbability = delayRiskProbability;
    }

    public Integer getPredictedDelayHours() {
        return predictedDelayHours;
    }

    public void setPredictedDelayHours(Integer predictedDelayHours) {
        this.predictedDelayHours = predictedDelayHours;
    }

    public Boolean getOnTimePerformance() {
        return onTimePerformance;
    }

    public void setOnTimePerformance(Boolean onTimePerformance) {
        this.onTimePerformance = onTimePerformance;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
    }

    public BigDecimal getFuelSurcharge() {
        return fuelSurcharge;
    }

    public void setFuelSurcharge(BigDecimal fuelSurcharge) {
        this.fuelSurcharge = fuelSurcharge;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getBilledWeightKg() {
        return billedWeightKg;
    }

    public void setBilledWeightKg(BigDecimal billedWeightKg) {
        this.billedWeightKg = billedWeightKg;
    }

    public BigDecimal getCarbonFootprintKg() {
        return carbonFootprintKg;
    }

    public void setCarbonFootprintKg(BigDecimal carbonFootprintKg) {
        this.carbonFootprintKg = carbonFootprintKg;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
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

    public Set<ShipmentTrackingEvent> getTrackingEvents() {
        return trackingEvents;
    }

    public void setTrackingEvents(Set<ShipmentTrackingEvent> trackingEvents) {
        this.trackingEvents = trackingEvents;
    }

    public Set<ShipmentItem> getItems() {
        return items;
    }

    public void setItems(Set<ShipmentItem> items) {
        this.items = items;
    }

    public Set<ShipmentDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<ShipmentDocument> documents) {
        this.documents = documents;
    }
}
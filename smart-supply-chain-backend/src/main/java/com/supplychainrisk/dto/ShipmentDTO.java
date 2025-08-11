package com.supplychainrisk.dto;

import com.supplychainrisk.entity.Shipment.ShipmentStatus;
import com.supplychainrisk.entity.Shipment.ShipmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ShipmentDTO {

    private Long id;

    @NotBlank(message = "Tracking number is required")
    private String trackingNumber;

    private String referenceNumber;
    private Long supplierId;
    private String supplierName;

    // Shipment Details
    private ShipmentType shipmentType;
    private String serviceLevel;
    private BigDecimal weightKg;
    private BigDecimal dimensionsLengthCm;
    private BigDecimal dimensionsWidthCm;
    private BigDecimal dimensionsHeightCm;
    private BigDecimal declaredValue;
    private String currency;

    // Origin Information
    private String originName;
    private String originAddress;
    private String originCity;
    private String originState;
    private String originCountry;
    private String originPostalCode;
    private BigDecimal originLatitude;
    private BigDecimal originLongitude;

    // Destination Information
    private String destinationName;
    private String destinationAddress;
    private String destinationCity;
    private String destinationState;
    private String destinationCountry;
    private String destinationPostalCode;
    private BigDecimal destinationLatitude;
    private BigDecimal destinationLongitude;

    // Carrier Information
    @NotBlank(message = "Carrier name is required")
    private String carrierName;
    private String carrierServiceCode;
    private String carrierTrackingUrl;

    // Status and Timing
    private ShipmentStatus status;
    private String substatus;
    private LocalDateTime shipDate;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private Integer transitDays;

    // Risk and Performance
    private Integer riskScore;
    private BigDecimal delayRiskProbability;
    private Integer predictedDelayHours;
    private Boolean onTimePerformance;

    // Cost and Billing
    private BigDecimal shippingCost;
    private BigDecimal fuelSurcharge;
    private BigDecimal totalCost;
    private BigDecimal billedWeightKg;

    // Environmental Impact
    private BigDecimal carbonFootprintKg;

    // Metadata
    private Long createdById;
    private String createdByName;
    private Long updatedById;
    private String updatedByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Related Data
    private List<ShipmentTrackingEventDTO> trackingEvents;
    private List<ShipmentItemDTO> items;
    private List<ShipmentDocumentDTO> documents;

    // Default constructor
    public ShipmentDTO() {}

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

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public Long getUpdatedById() {
        return updatedById;
    }

    public void setUpdatedById(Long updatedById) {
        this.updatedById = updatedById;
    }

    public String getUpdatedByName() {
        return updatedByName;
    }

    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
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

    public List<ShipmentTrackingEventDTO> getTrackingEvents() {
        return trackingEvents;
    }

    public void setTrackingEvents(List<ShipmentTrackingEventDTO> trackingEvents) {
        this.trackingEvents = trackingEvents;
    }

    public List<ShipmentItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ShipmentItemDTO> items) {
        this.items = items;
    }

    public List<ShipmentDocumentDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(List<ShipmentDocumentDTO> documents) {
        this.documents = documents;
    }
}
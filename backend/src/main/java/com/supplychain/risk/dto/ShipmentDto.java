package com.supplychain.risk.dto;

import com.supplychain.risk.enums.ShipmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ShipmentDto {
    
    private Long id;
    
    @NotBlank(message = "Tracking number is required")
    private String trackingNumber;
    
    @NotNull(message = "Supplier ID is required")
    private Long supplierId;
    
    private String supplierName;
    
    @NotBlank(message = "Product name is required")
    private String productName;
    
    private String productCategory;
    
    @PositiveOrZero(message = "Quantity must be positive or zero")
    private Integer quantity;
    
    @PositiveOrZero(message = "Value must be positive or zero")
    private BigDecimal value;
    
    @NotBlank(message = "Origin address is required")
    private String originAddress;
    
    @NotBlank(message = "Destination address is required")
    private String destinationAddress;
    
    @NotNull(message = "Shipment status is required")
    private ShipmentStatus status;
    
    private LocalDate expectedDeliveryDate;
    private LocalDate actualDeliveryDate;
    private LocalDate shippedDate;
    private String carrierName;
    private String currentLocation;
    private LocalDateTime estimatedArrival;
    private String delayReason;
    private String specialInstructions;
    private BigDecimal insuranceCoverage;
    private Boolean isPriority;
    private Boolean temperatureControlled;
    private Boolean customsCleared;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public ShipmentDto() {}
    
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
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductCategory() {
        return productCategory;
    }
    
    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getValue() {
        return value;
    }
    
    public void setValue(BigDecimal value) {
        this.value = value;
    }
    
    public String getOriginAddress() {
        return originAddress;
    }
    
    public void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }
    
    public String getDestinationAddress() {
        return destinationAddress;
    }
    
    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }
    
    public ShipmentStatus getStatus() {
        return status;
    }
    
    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }
    
    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }
    
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }
    
    public LocalDate getActualDeliveryDate() {
        return actualDeliveryDate;
    }
    
    public void setActualDeliveryDate(LocalDate actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }
    
    public LocalDate getShippedDate() {
        return shippedDate;
    }
    
    public void setShippedDate(LocalDate shippedDate) {
        this.shippedDate = shippedDate;
    }
    
    public String getCarrierName() {
        return carrierName;
    }
    
    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }
    
    public String getCurrentLocation() {
        return currentLocation;
    }
    
    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }
    
    public LocalDateTime getEstimatedArrival() {
        return estimatedArrival;
    }
    
    public void setEstimatedArrival(LocalDateTime estimatedArrival) {
        this.estimatedArrival = estimatedArrival;
    }
    
    public String getDelayReason() {
        return delayReason;
    }
    
    public void setDelayReason(String delayReason) {
        this.delayReason = delayReason;
    }
    
    public String getSpecialInstructions() {
        return specialInstructions;
    }
    
    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
    
    public BigDecimal getInsuranceCoverage() {
        return insuranceCoverage;
    }
    
    public void setInsuranceCoverage(BigDecimal insuranceCoverage) {
        this.insuranceCoverage = insuranceCoverage;
    }
    
    public Boolean getIsPriority() {
        return isPriority;
    }
    
    public void setIsPriority(Boolean isPriority) {
        this.isPriority = isPriority;
    }
    
    public Boolean getTemperatureControlled() {
        return temperatureControlled;
    }
    
    public void setTemperatureControlled(Boolean temperatureControlled) {
        this.temperatureControlled = temperatureControlled;
    }
    
    public Boolean getCustomsCleared() {
        return customsCleared;
    }
    
    public void setCustomsCleared(Boolean customsCleared) {
        this.customsCleared = customsCleared;
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
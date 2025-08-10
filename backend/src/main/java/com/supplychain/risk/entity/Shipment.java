package com.supplychain.risk.entity;

import com.supplychain.risk.enums.ShipmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@EntityListeners(AuditingEntityListener.class)
public class Shipment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tracking_number", unique = true, nullable = false)
    @NotBlank(message = "Tracking number is required")
    private String trackingNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    @NotNull(message = "Supplier is required")
    private Supplier supplier;
    
    @Column(name = "product_name", nullable = false)
    @NotBlank(message = "Product name is required")
    private String productName;
    
    @Column(name = "product_category")
    private String productCategory;
    
    @Column(nullable = false)
    @PositiveOrZero(message = "Quantity must be positive or zero")
    private Integer quantity;
    
    @Column(precision = 10, scale = 2)
    @PositiveOrZero(message = "Value must be positive or zero")
    private BigDecimal value;
    
    @Column(name = "origin_address", nullable = false)
    @NotBlank(message = "Origin address is required")
    private String originAddress;
    
    @Column(name = "destination_address", nullable = false)
    @NotBlank(message = "Destination address is required")
    private String destinationAddress;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Shipment status is required")
    private ShipmentStatus status;
    
    @Column(name = "expected_delivery_date")
    private LocalDate expectedDeliveryDate;
    
    @Column(name = "actual_delivery_date")
    private LocalDate actualDeliveryDate;
    
    @Column(name = "shipped_date")
    private LocalDate shippedDate;
    
    @Column(name = "carrier_name")
    private String carrierName;
    
    @Column(name = "current_location")
    private String currentLocation;
    
    @Column(name = "estimated_arrival")
    private LocalDateTime estimatedArrival;
    
    @Column(name = "delay_reason")
    private String delayReason;
    
    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;
    
    @Column(name = "insurance_coverage", precision = 10, scale = 2)
    private BigDecimal insuranceCoverage;
    
    @Column(name = "is_priority", nullable = false)
    private Boolean isPriority = false;
    
    @Column(name = "temperature_controlled")
    private Boolean temperatureControlled = false;
    
    @Column(name = "customs_cleared")
    private Boolean customsCleared = false;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Shipment() {}
    
    public Shipment(String trackingNumber, Supplier supplier, String productName, 
                   Integer quantity, String originAddress, String destinationAddress, 
                   ShipmentStatus status) {
        this.trackingNumber = trackingNumber;
        this.supplier = supplier;
        this.productName = productName;
        this.quantity = quantity;
        this.originAddress = originAddress;
        this.destinationAddress = destinationAddress;
        this.status = status;
    }
    
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
    
    public Supplier getSupplier() {
        return supplier;
    }
    
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
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
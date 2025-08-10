package com.smartsupplychain.riskintelligence.dto;

import com.smartsupplychain.riskintelligence.enums.ShipmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ShipmentDto {
    private Long id;
    
    @NotNull(message = "Supplier ID is required")
    private Long supplierId;
    
    private String supplierName;
    
    @NotNull(message = "Status is required")
    private ShipmentStatus status;
    
    @NotBlank(message = "Origin is required")
    private String origin;
    
    @NotBlank(message = "Destination is required")
    private String destination;
    
    private LocalDateTime estimatedArrival;
    private LocalDateTime actualArrival;
    private String trackingNumber;
    private String description;
    private LocalDateTime lastUpdate;

    // Constructors
    public ShipmentDto() {}

    public ShipmentDto(Long supplierId, String origin, String destination, LocalDateTime estimatedArrival) {
        this.supplierId = supplierId;
        this.origin = origin;
        this.destination = destination;
        this.estimatedArrival = estimatedArrival;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getEstimatedArrival() {
        return estimatedArrival;
    }

    public void setEstimatedArrival(LocalDateTime estimatedArrival) {
        this.estimatedArrival = estimatedArrival;
    }

    public LocalDateTime getActualArrival() {
        return actualArrival;
    }

    public void setActualArrival(LocalDateTime actualArrival) {
        this.actualArrival = actualArrival;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
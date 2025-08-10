package com.smartsupplychain.riskintelligence.model;

import com.smartsupplychain.riskintelligence.enums.ShipmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    @NotNull(message = "Supplier is required")
    private Supplier supplier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Status is required")
    private ShipmentStatus status = ShipmentStatus.PENDING;

    @Column(nullable = false)
    @NotBlank(message = "Origin is required")
    private String origin;

    @Column(nullable = false)
    @NotBlank(message = "Destination is required")
    private String destination;

    @Column(name = "estimated_arrival")
    private LocalDateTime estimatedArrival;

    @Column(name = "actual_arrival")
    private LocalDateTime actualArrival;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(columnDefinition = "TEXT")
    private String description;

    @UpdateTimestamp
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    // Constructors
    public Shipment() {}

    public Shipment(Supplier supplier, String origin, String destination, LocalDateTime estimatedArrival) {
        this.supplier = supplier;
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

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
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
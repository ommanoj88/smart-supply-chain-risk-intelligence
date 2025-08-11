package com.supplychainrisk.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipment_items")
public class ShipmentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shipment_id", nullable = false)
    @NotNull(message = "Shipment is required")
    private Shipment shipment;

    @Column(name = "item_name", nullable = false)
    @NotBlank(message = "Item name is required")
    private String itemName;

    @Column(name = "item_description", columnDefinition = "TEXT")
    private String itemDescription;

    @Column(nullable = false)
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity = 1;

    @Column(name = "weight_kg", precision = 10, scale = 3)
    private BigDecimal weightKg;

    @Column(name = "unit_value", precision = 10, scale = 2)
    private BigDecimal unitValue;

    @Column(name = "total_value", precision = 10, scale = 2)
    private BigDecimal totalValue;

    @Column(length = 100)
    private String sku;

    @Column(name = "hs_code", length = 20)
    private String hsCode; // Harmonized System code for customs

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Default constructor
    public ShipmentItem() {}

    // Constructor with required fields
    public ShipmentItem(Shipment shipment, String itemName, Integer quantity) {
        this.shipment = shipment;
        this.itemName = itemName;
        this.quantity = quantity;
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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public BigDecimal getUnitValue() {
        return unitValue;
    }

    public void setUnitValue(BigDecimal unitValue) {
        this.unitValue = unitValue;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getHsCode() {
        return hsCode;
    }

    public void setHsCode(String hsCode) {
        this.hsCode = hsCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
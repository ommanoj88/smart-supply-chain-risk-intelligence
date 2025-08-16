package com.supplychainrisk.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "supplier_locations")
public class SupplierLocation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    @NotNull
    private Supplier supplier;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocationType type = LocationType.HEADQUARTERS;
    
    @Column(nullable = false)
    @NotBlank(message = "Location name is required")
    private String name;
    
    @Column(name = "street_address", columnDefinition = "TEXT")
    private String address;
    
    @Column(length = 100)
    private String city;
    
    @Column(name = "state_province", length = 100)
    private String state;
    
    @Column(length = 100)
    private String country;
    
    @Column(name = "postal_code", length = 20)
    private String postalCode;
    
    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;
    
    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;
    
    @Column(name = "is_primary")
    private Boolean isPrimary = false;
    
    @Column(columnDefinition = "TEXT")
    private String capabilities;
    
    @Column(name = "capacity_units")
    private Integer capacity;
    
    @Column(name = "capacity_unit_type", length = 50)
    private String capacityUnitType;
    
    public enum LocationType {
        HEADQUARTERS, MANUFACTURING, WAREHOUSE, DISTRIBUTION_CENTER, R_AND_D, OFFICE
    }
    
    // Default constructor
    public SupplierLocation() {}
    
    // Constructor with required fields
    public SupplierLocation(Supplier supplier, String name, LocationType type) {
        this.supplier = supplier;
        this.name = name;
        this.type = type;
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
    
    public LocationType getType() {
        return type;
    }
    
    public void setType(LocationType type) {
        this.type = type;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
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
    
    public Boolean getIsPrimary() {
        return isPrimary;
    }
    
    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
    
    public String getCapabilities() {
        return capabilities;
    }
    
    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    public String getCapacityUnitType() {
        return capacityUnitType;
    }
    
    public void setCapacityUnitType(String capacityUnitType) {
        this.capacityUnitType = capacityUnitType;
    }
}
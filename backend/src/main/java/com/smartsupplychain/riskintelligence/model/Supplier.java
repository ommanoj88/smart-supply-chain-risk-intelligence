package com.smartsupplychain.riskintelligence.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Supplier name is required")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "Location is required")
    private String location;

    @Column(name = "contact_info")
    private String contactInfo;

    @Column(name = "risk_score", precision = 3, scale = 2)
    @DecimalMin(value = "0.0", message = "Risk score must be between 0 and 10")
    @DecimalMax(value = "10.0", message = "Risk score must be between 0 and 10")
    private BigDecimal riskScore = BigDecimal.ZERO;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    // Constructors
    public Supplier() {}

    public Supplier(String name, String location, String contactInfo) {
        this.name = name;
        this.location = location;
        this.contactInfo = contactInfo;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public BigDecimal getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(BigDecimal riskScore) {
        this.riskScore = riskScore;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
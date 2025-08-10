package com.smartsupplychain.riskintelligence.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public class SupplierDto {
    private Long id;
    
    @NotBlank(message = "Supplier name is required")
    private String name;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    private String contactInfo;
    
    @DecimalMin(value = "0.0", message = "Risk score must be between 0 and 10")
    @DecimalMax(value = "10.0", message = "Risk score must be between 0 and 10")
    private BigDecimal riskScore;
    
    private Boolean isActive;

    // Constructors
    public SupplierDto() {}

    public SupplierDto(String name, String location, String contactInfo) {
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
}
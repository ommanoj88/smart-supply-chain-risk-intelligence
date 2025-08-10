package com.supplychain.risk.dto;

import com.supplychain.risk.enums.SupplierStatus;
import com.supplychain.risk.enums.RiskLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SupplierDto {
    
    private Long id;
    
    @NotBlank(message = "Supplier name is required")
    private String name;
    
    private String contactPerson;
    private String contactEmail;
    private String contactPhone;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    @NotBlank(message = "Country is required")
    private String country;
    
    @NotBlank(message = "City is required")
    private String city;
    
    private String postalCode;
    
    @NotNull(message = "Supplier status is required")
    private SupplierStatus status;
    
    @DecimalMin(value = "0.0", message = "Risk score must be between 0.0 and 10.0")
    @DecimalMax(value = "10.0", message = "Risk score must be between 0.0 and 10.0")
    private BigDecimal riskScore;
    
    private RiskLevel riskLevel;
    private String certificationLevel;
    private String financialRating;
    private BigDecimal deliveryPerformance;
    private BigDecimal qualityRating;
    private Integer totalOrders;
    private Integer successfulDeliveries;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public SupplierDto() {}
    
    public SupplierDto(String name, String address, String country, String city, SupplierStatus status) {
        this.name = name;
        this.address = address;
        this.country = country;
        this.city = city;
        this.status = status;
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
    
    public String getContactPerson() {
        return contactPerson;
    }
    
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
    
    public String getContactEmail() {
        return contactEmail;
    }
    
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    
    public String getContactPhone() {
        return contactPhone;
    }
    
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public SupplierStatus getStatus() {
        return status;
    }
    
    public void setStatus(SupplierStatus status) {
        this.status = status;
    }
    
    public BigDecimal getRiskScore() {
        return riskScore;
    }
    
    public void setRiskScore(BigDecimal riskScore) {
        this.riskScore = riskScore;
    }
    
    public RiskLevel getRiskLevel() {
        return riskLevel;
    }
    
    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }
    
    public String getCertificationLevel() {
        return certificationLevel;
    }
    
    public void setCertificationLevel(String certificationLevel) {
        this.certificationLevel = certificationLevel;
    }
    
    public String getFinancialRating() {
        return financialRating;
    }
    
    public void setFinancialRating(String financialRating) {
        this.financialRating = financialRating;
    }
    
    public BigDecimal getDeliveryPerformance() {
        return deliveryPerformance;
    }
    
    public void setDeliveryPerformance(BigDecimal deliveryPerformance) {
        this.deliveryPerformance = deliveryPerformance;
    }
    
    public BigDecimal getQualityRating() {
        return qualityRating;
    }
    
    public void setQualityRating(BigDecimal qualityRating) {
        this.qualityRating = qualityRating;
    }
    
    public Integer getTotalOrders() {
        return totalOrders;
    }
    
    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }
    
    public Integer getSuccessfulDeliveries() {
        return successfulDeliveries;
    }
    
    public void setSuccessfulDeliveries(Integer successfulDeliveries) {
        this.successfulDeliveries = successfulDeliveries;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
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
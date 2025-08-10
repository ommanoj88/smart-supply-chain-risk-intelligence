package com.supplychain.risk.entity;

import com.supplychain.risk.enums.SupplierStatus;
import com.supplychain.risk.enums.RiskLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "suppliers")
@EntityListeners(AuditingEntityListener.class)
public class Supplier {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    @NotBlank(message = "Supplier name is required")
    private String name;
    
    @Column(name = "contact_person")
    private String contactPerson;
    
    @Column(name = "contact_email")
    private String contactEmail;
    
    @Column(name = "contact_phone")
    private String contactPhone;
    
    @Column(nullable = false)
    @NotBlank(message = "Address is required")
    private String address;
    
    @Column(nullable = false)
    @NotBlank(message = "Country is required")
    private String country;
    
    @Column(nullable = false)
    @NotBlank(message = "City is required")
    private String city;
    
    @Column(name = "postal_code")
    private String postalCode;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Supplier status is required")
    private SupplierStatus status;
    
    @Column(name = "risk_score", precision = 3, scale = 2)
    @DecimalMin(value = "0.0", message = "Risk score must be between 0.0 and 10.0")
    @DecimalMax(value = "10.0", message = "Risk score must be between 0.0 and 10.0")
    private BigDecimal riskScore;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level")
    private RiskLevel riskLevel;
    
    @Column(name = "certification_level")
    private String certificationLevel;
    
    @Column(name = "financial_rating")
    private String financialRating;
    
    @Column(name = "delivery_performance", precision = 5, scale = 2)
    private BigDecimal deliveryPerformance;
    
    @Column(name = "quality_rating", precision = 3, scale = 2)
    private BigDecimal qualityRating;
    
    @Column(name = "total_orders")
    private Integer totalOrders = 0;
    
    @Column(name = "successful_deliveries")
    private Integer successfulDeliveries = 0;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Shipment> shipments;
    
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RiskAssessment> riskAssessments;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Supplier() {}
    
    public Supplier(String name, String address, String country, String city, SupplierStatus status) {
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
    
    public List<Shipment> getShipments() {
        return shipments;
    }
    
    public void setShipments(List<Shipment> shipments) {
        this.shipments = shipments;
    }
    
    public List<RiskAssessment> getRiskAssessments() {
        return riskAssessments;
    }
    
    public void setRiskAssessments(List<RiskAssessment> riskAssessments) {
        this.riskAssessments = riskAssessments;
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
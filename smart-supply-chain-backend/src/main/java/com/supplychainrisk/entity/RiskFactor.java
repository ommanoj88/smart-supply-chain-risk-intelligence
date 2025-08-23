package com.supplychainrisk.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

@Entity
@Table(name = "supplier_risk_factors")
public class RiskFactor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    @NotNull
    private Supplier supplier;
    
    @Column(name = "factor_type", nullable = false, length = 50)
    @NotBlank(message = "Risk factor type is required")
    private String factorType;
    
    @Column(name = "factor_name", nullable = false, length = 100)
    @NotBlank(message = "Risk factor name is required")
    private String factorName;
    
    @Column(name = "risk_score", nullable = false)
    @Min(value = 0, message = "Risk score cannot be negative")
    @Max(value = 100, message = "Risk score cannot exceed 100")
    private Integer riskScore;
    
    @Column(name = "weight", nullable = false)
    @Min(value = 0, message = "Weight cannot be negative")
    @Max(value = 100, message = "Weight cannot exceed 100")
    private Integer weight;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private Severity severity = Severity.MEDIUM;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "mitigation_strategy", columnDefinition = "TEXT")
    private String mitigationStrategy;
    
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    public enum Severity {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    // Default constructor
    public RiskFactor() {}
    
    // Constructor with required fields
    public RiskFactor(Supplier supplier, String factorType, String factorName, Integer riskScore, Integer weight) {
        this.supplier = supplier;
        this.factorType = factorType;
        this.factorName = factorName;
        this.riskScore = riskScore;
        this.weight = weight;
        this.lastUpdated = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
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
    
    public String getFactorType() {
        return factorType;
    }
    
    public void setFactorType(String factorType) {
        this.factorType = factorType;
    }
    
    public String getFactorName() {
        return factorName;
    }
    
    public void setFactorName(String factorName) {
        this.factorName = factorName;
    }
    
    public Integer getRiskScore() {
        return riskScore;
    }
    
    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }
    
    public Integer getWeight() {
        return weight;
    }
    
    public void setWeight(Integer weight) {
        this.weight = weight;
    }
    
    public Severity getSeverity() {
        return severity;
    }
    
    public void setSeverity(Severity severity) {
        this.severity = severity;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getMitigationStrategy() {
        return mitigationStrategy;
    }
    
    public void setMitigationStrategy(String mitigationStrategy) {
        this.mitigationStrategy = mitigationStrategy;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
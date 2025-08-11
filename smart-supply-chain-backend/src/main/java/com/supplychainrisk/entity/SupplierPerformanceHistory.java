package com.supplychainrisk.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "supplier_performance_history")
public class SupplierPerformanceHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    @NotNull(message = "Supplier is required")
    private Supplier supplier;
    
    @Column(name = "performance_date", nullable = false)
    @NotNull(message = "Performance date is required")
    private LocalDate performanceDate;
    
    @Column(name = "on_time_delivery_rate", precision = 5, scale = 2)
    @DecimalMin(value = "0.00", message = "Delivery rate cannot be negative")
    @DecimalMax(value = "100.00", message = "Delivery rate cannot exceed 100%")
    private BigDecimal onTimeDeliveryRate;
    
    @Column(name = "quality_score", precision = 5, scale = 2)
    @DecimalMin(value = "0.00")
    @DecimalMax(value = "10.00")
    private BigDecimal qualityScore;
    
    @Column(name = "cost_score")
    private Integer costScore;
    
    @Column(name = "overall_score", precision = 5, scale = 2)
    @DecimalMin(value = "0.00")
    @DecimalMax(value = "10.00")
    private BigDecimal overallScore;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Default constructor
    public SupplierPerformanceHistory() {}
    
    // Constructor with essential fields
    public SupplierPerformanceHistory(Supplier supplier, LocalDate performanceDate) {
        this.supplier = supplier;
        this.performanceDate = performanceDate;
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
    
    public LocalDate getPerformanceDate() {
        return performanceDate;
    }
    
    public void setPerformanceDate(LocalDate performanceDate) {
        this.performanceDate = performanceDate;
    }
    
    public BigDecimal getOnTimeDeliveryRate() {
        return onTimeDeliveryRate;
    }
    
    public void setOnTimeDeliveryRate(BigDecimal onTimeDeliveryRate) {
        this.onTimeDeliveryRate = onTimeDeliveryRate;
    }
    
    public BigDecimal getQualityScore() {
        return qualityScore;
    }
    
    public void setQualityScore(BigDecimal qualityScore) {
        this.qualityScore = qualityScore;
    }
    
    public Integer getCostScore() {
        return costScore;
    }
    
    public void setCostScore(Integer costScore) {
        this.costScore = costScore;
    }
    
    public BigDecimal getOverallScore() {
        return overallScore;
    }
    
    public void setOverallScore(BigDecimal overallScore) {
        this.overallScore = overallScore;
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
}
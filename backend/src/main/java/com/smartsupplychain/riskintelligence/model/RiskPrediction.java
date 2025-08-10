package com.smartsupplychain.riskintelligence.model;

import com.smartsupplychain.riskintelligence.enums.RiskLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_predictions")
public class RiskPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false)
    private RiskLevel riskLevel;

    @Column(precision = 5, scale = 4)
    @DecimalMin(value = "0.0", message = "Probability must be between 0 and 1")
    @DecimalMax(value = "1.0", message = "Probability must be between 0 and 1")
    private BigDecimal probability;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "prediction_factors", columnDefinition = "TEXT")
    private String predictionFactors;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public RiskPrediction() {}

    public RiskPrediction(Shipment shipment, RiskLevel riskLevel, BigDecimal probability, String description) {
        this.shipment = shipment;
        this.riskLevel = riskLevel;
        this.probability = probability;
        this.description = description;
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

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public BigDecimal getProbability() {
        return probability;
    }

    public void setProbability(BigDecimal probability) {
        this.probability = probability;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPredictionFactors() {
        return predictionFactors;
    }

    public void setPredictionFactors(String predictionFactors) {
        this.predictionFactors = predictionFactors;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
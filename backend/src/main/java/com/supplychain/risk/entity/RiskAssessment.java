package com.supplychain.risk.entity;

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

@Entity
@Table(name = "risk_assessments")
@EntityListeners(AuditingEntityListener.class)
public class RiskAssessment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    @NotNull(message = "Supplier is required")
    private Supplier supplier;
    
    @Column(name = "assessment_type", nullable = false)
    @NotBlank(message = "Assessment type is required")
    private String assessmentType; // FINANCIAL, OPERATIONAL, GEOPOLITICAL, COMPLIANCE
    
    @Column(name = "risk_score", precision = 3, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "Risk score must be between 0.0 and 10.0")
    @DecimalMax(value = "10.0", message = "Risk score must be between 0.0 and 10.0")
    @NotNull(message = "Risk score is required")
    private BigDecimal riskScore;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false)
    @NotNull(message = "Risk level is required")
    private RiskLevel riskLevel;
    
    @Column(name = "assessment_criteria", columnDefinition = "TEXT")
    private String assessmentCriteria;
    
    @Column(name = "risk_factors", columnDefinition = "TEXT")
    private String riskFactors;
    
    @Column(name = "mitigation_strategies", columnDefinition = "TEXT")
    private String mitigationStrategies;
    
    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;
    
    @Column(name = "next_review_date")
    private LocalDateTime nextReviewDate;
    
    @Column(name = "reviewed_by")
    private String reviewedBy;
    
    @Column(name = "financial_stability_score", precision = 3, scale = 2)
    private BigDecimal financialStabilityScore;
    
    @Column(name = "operational_risk_score", precision = 3, scale = 2)
    private BigDecimal operationalRiskScore;
    
    @Column(name = "compliance_score", precision = 3, scale = 2)
    private BigDecimal complianceScore;
    
    @Column(name = "geopolitical_risk_score", precision = 3, scale = 2)
    private BigDecimal geopoliticalRiskScore;
    
    @Column(name = "environmental_risk_score", precision = 3, scale = 2)
    private BigDecimal environmentalRiskScore;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "assessment_version")
    private Integer assessmentVersion = 1;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public RiskAssessment() {}
    
    public RiskAssessment(Supplier supplier, String assessmentType, BigDecimal riskScore, RiskLevel riskLevel) {
        this.supplier = supplier;
        this.assessmentType = assessmentType;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
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
    
    public String getAssessmentType() {
        return assessmentType;
    }
    
    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
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
    
    public String getAssessmentCriteria() {
        return assessmentCriteria;
    }
    
    public void setAssessmentCriteria(String assessmentCriteria) {
        this.assessmentCriteria = assessmentCriteria;
    }
    
    public String getRiskFactors() {
        return riskFactors;
    }
    
    public void setRiskFactors(String riskFactors) {
        this.riskFactors = riskFactors;
    }
    
    public String getMitigationStrategies() {
        return mitigationStrategies;
    }
    
    public void setMitigationStrategies(String mitigationStrategies) {
        this.mitigationStrategies = mitigationStrategies;
    }
    
    public String getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }
    
    public LocalDateTime getNextReviewDate() {
        return nextReviewDate;
    }
    
    public void setNextReviewDate(LocalDateTime nextReviewDate) {
        this.nextReviewDate = nextReviewDate;
    }
    
    public String getReviewedBy() {
        return reviewedBy;
    }
    
    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
    
    public BigDecimal getFinancialStabilityScore() {
        return financialStabilityScore;
    }
    
    public void setFinancialStabilityScore(BigDecimal financialStabilityScore) {
        this.financialStabilityScore = financialStabilityScore;
    }
    
    public BigDecimal getOperationalRiskScore() {
        return operationalRiskScore;
    }
    
    public void setOperationalRiskScore(BigDecimal operationalRiskScore) {
        this.operationalRiskScore = operationalRiskScore;
    }
    
    public BigDecimal getComplianceScore() {
        return complianceScore;
    }
    
    public void setComplianceScore(BigDecimal complianceScore) {
        this.complianceScore = complianceScore;
    }
    
    public BigDecimal getGeopoliticalRiskScore() {
        return geopoliticalRiskScore;
    }
    
    public void setGeopoliticalRiskScore(BigDecimal geopoliticalRiskScore) {
        this.geopoliticalRiskScore = geopoliticalRiskScore;
    }
    
    public BigDecimal getEnvironmentalRiskScore() {
        return environmentalRiskScore;
    }
    
    public void setEnvironmentalRiskScore(BigDecimal environmentalRiskScore) {
        this.environmentalRiskScore = environmentalRiskScore;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Integer getAssessmentVersion() {
        return assessmentVersion;
    }
    
    public void setAssessmentVersion(Integer assessmentVersion) {
        this.assessmentVersion = assessmentVersion;
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
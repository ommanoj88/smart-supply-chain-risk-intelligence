package com.supplychainrisk.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "suppliers")
public class Supplier {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "supplier_code", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Supplier code is required")
    private String supplierCode;
    
    @Column(nullable = false)
    @NotBlank(message = "Supplier name is required")
    private String name;
    
    @Column(name = "legal_name")
    private String legalName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplierTier tier = SupplierTier.SECONDARY;
    
    // Contact Information
    @Column(name = "primary_contact_name")
    private String primaryContactName;
    
    @Column(name = "primary_contact_email")
    @Email(message = "Invalid email format")
    private String primaryContactEmail;
    
    @Column(name = "primary_contact_phone", length = 50)
    private String primaryContactPhone;
    
    @Column(name = "secondary_contact_name")
    private String secondaryContactName;
    
    @Column(name = "secondary_contact_email")
    @Email(message = "Invalid email format")
    private String secondaryContactEmail;
    
    @Column(name = "secondary_contact_phone", length = 50)
    private String secondaryContactPhone;
    
    private String website;
    
    // Address Information
    @Column(name = "street_address", columnDefinition = "TEXT")
    private String streetAddress;
    
    @Column(length = 100)
    private String city;
    
    @Column(name = "state_province", length = 100)
    private String stateProvince;
    
    @Column(name = "postal_code", length = 20)
    private String postalCode;
    
    @Column(length = 100)
    private String country;
    
    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;
    
    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;
    
    // Business Information
    @Column(length = 100)
    private String industry;
    
    @Column(name = "business_type", length = 50)
    private String businessType;
    
    @Column(name = "annual_revenue", precision = 15, scale = 2)
    private BigDecimal annualRevenue;
    
    @Column(name = "employee_count")
    private Integer employeeCount;
    
    @Column(name = "years_in_business")
    private Integer yearsInBusiness;
    
    // Risk and Performance Metrics
    @Column(name = "overall_risk_score")
    @Min(value = 0, message = "Risk score cannot be negative")
    @Max(value = 100, message = "Risk score cannot exceed 100")
    private Integer overallRiskScore = 0;
    
    @Column(name = "financial_risk_score")
    @Min(value = 0)
    @Max(value = 100)
    private Integer financialRiskScore = 0;
    
    @Column(name = "operational_risk_score")
    @Min(value = 0)
    @Max(value = 100)
    private Integer operationalRiskScore = 0;
    
    @Column(name = "compliance_risk_score")
    @Min(value = 0)
    @Max(value = 100)
    private Integer complianceRiskScore = 0;
    
    @Column(name = "geographic_risk_score")
    @Min(value = 0)
    @Max(value = 100)
    private Integer geographicRiskScore = 0;
    
    // Performance KPIs
    @Column(name = "on_time_delivery_rate", precision = 5, scale = 2)
    @DecimalMin(value = "0.00", message = "Delivery rate cannot be negative")
    @DecimalMax(value = "100.00", message = "Delivery rate cannot exceed 100%")
    private BigDecimal onTimeDeliveryRate = BigDecimal.ZERO;
    
    @Column(name = "quality_rating", precision = 5, scale = 2)
    @DecimalMin(value = "0.00")
    @DecimalMax(value = "10.00")
    private BigDecimal qualityRating = BigDecimal.ZERO;
    
    @Column(name = "cost_competitiveness_score")
    @Min(value = 0)
    @Max(value = 100)
    private Integer costCompetitivenessScore = 0;
    
    @Column(name = "responsiveness_score")
    @Min(value = 0)
    @Max(value = 100)
    private Integer responsivenessScore = 0;
    
    // Certifications and Compliance
    @ElementCollection
    @Column(name = "iso_certifications")
    private List<String> isoCertifications;
    
    @ElementCollection
    @Column(name = "compliance_certifications")
    private List<String> complianceCertifications;
    
    @Column(name = "last_audit_date")
    private LocalDate lastAuditDate;
    
    @Column(name = "next_audit_due_date")
    private LocalDate nextAuditDueDate;
    
    // Financial Information
    @Column(name = "credit_rating", length = 10)
    private String creditRating;
    
    @Column(name = "payment_terms", length = 100)
    private String paymentTerms;
    
    @Column(length = 3)
    private String currency = "USD";
    
    // Status and Metadata
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplierStatus status = SupplierStatus.ACTIVE;
    
    @Column(name = "preferred_supplier")
    private Boolean preferredSupplier = false;
    
    @Column(name = "strategic_supplier")
    private Boolean strategicSupplier = false;
    
    // Audit Fields
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @ManyToOne
    @JoinColumn(name = "updated_by")
    private User updatedBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enhanced relationship fields for new entities
    @Column(name = "risk_score_double", precision = 5, scale = 2)
    private Double riskScore;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level")
    private RiskLevel riskLevel;
    
    @Column(name = "risk_last_updated")
    private LocalDateTime riskLastUpdated;
    
    // Supplier relationship fields
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_supplier_id")
    private Supplier parentSupplier;
    
    @OneToMany(mappedBy = "parentSupplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Supplier> subsidiaries;
    
    // Relationships
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<SupplierLocation> locations;
    
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<RiskFactor> riskFactors;
    
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<SupplierCertification> certifications;
    
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<SupplierPerformanceHistory> performanceHistory;
    
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<SupplierDocument> documents;
    
    @ManyToMany
    @JoinTable(
        name = "supplier_category_mapping",
        joinColumns = @JoinColumn(name = "supplier_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<SupplierCategory> categories;
    
    // Enums
    public enum SupplierTier {
        PRIMARY, SECONDARY, BACKUP
    }
    
    public enum SupplierStatus {
        ACTIVE, INACTIVE, PENDING, BLOCKED
    }
    
    public enum RiskLevel {
        VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Default constructor
    public Supplier() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSupplierCode() {
        return supplierCode;
    }
    
    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLegalName() {
        return legalName;
    }
    
    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }
    
    public SupplierTier getTier() {
        return tier;
    }
    
    public void setTier(SupplierTier tier) {
        this.tier = tier;
    }
    
    public String getPrimaryContactName() {
        return primaryContactName;
    }
    
    public void setPrimaryContactName(String primaryContactName) {
        this.primaryContactName = primaryContactName;
    }
    
    public String getPrimaryContactEmail() {
        return primaryContactEmail;
    }
    
    public void setPrimaryContactEmail(String primaryContactEmail) {
        this.primaryContactEmail = primaryContactEmail;
    }
    
    public String getPrimaryContactPhone() {
        return primaryContactPhone;
    }
    
    public void setPrimaryContactPhone(String primaryContactPhone) {
        this.primaryContactPhone = primaryContactPhone;
    }
    
    public String getSecondaryContactName() {
        return secondaryContactName;
    }
    
    public void setSecondaryContactName(String secondaryContactName) {
        this.secondaryContactName = secondaryContactName;
    }
    
    public String getSecondaryContactEmail() {
        return secondaryContactEmail;
    }
    
    public void setSecondaryContactEmail(String secondaryContactEmail) {
        this.secondaryContactEmail = secondaryContactEmail;
    }
    
    public String getSecondaryContactPhone() {
        return secondaryContactPhone;
    }
    
    public void setSecondaryContactPhone(String secondaryContactPhone) {
        this.secondaryContactPhone = secondaryContactPhone;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public String getStreetAddress() {
        return streetAddress;
    }
    
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getStateProvince() {
        return stateProvince;
    }
    
    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
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
    
    public String getIndustry() {
        return industry;
    }
    
    public void setIndustry(String industry) {
        this.industry = industry;
    }
    
    public String getBusinessType() {
        return businessType;
    }
    
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }
    
    public BigDecimal getAnnualRevenue() {
        return annualRevenue;
    }
    
    public void setAnnualRevenue(BigDecimal annualRevenue) {
        this.annualRevenue = annualRevenue;
    }
    
    public Integer getEmployeeCount() {
        return employeeCount;
    }
    
    public void setEmployeeCount(Integer employeeCount) {
        this.employeeCount = employeeCount;
    }
    
    public Integer getYearsInBusiness() {
        return yearsInBusiness;
    }
    
    public void setYearsInBusiness(Integer yearsInBusiness) {
        this.yearsInBusiness = yearsInBusiness;
    }
    
    public Integer getOverallRiskScore() {
        return overallRiskScore;
    }
    
    public void setOverallRiskScore(Integer overallRiskScore) {
        this.overallRiskScore = overallRiskScore;
    }
    
    public Integer getFinancialRiskScore() {
        return financialRiskScore;
    }
    
    public void setFinancialRiskScore(Integer financialRiskScore) {
        this.financialRiskScore = financialRiskScore;
    }
    
    public Integer getOperationalRiskScore() {
        return operationalRiskScore;
    }
    
    public void setOperationalRiskScore(Integer operationalRiskScore) {
        this.operationalRiskScore = operationalRiskScore;
    }
    
    public Integer getComplianceRiskScore() {
        return complianceRiskScore;
    }
    
    public void setComplianceRiskScore(Integer complianceRiskScore) {
        this.complianceRiskScore = complianceRiskScore;
    }
    
    public Integer getGeographicRiskScore() {
        return geographicRiskScore;
    }
    
    public void setGeographicRiskScore(Integer geographicRiskScore) {
        this.geographicRiskScore = geographicRiskScore;
    }
    
    public BigDecimal getOnTimeDeliveryRate() {
        return onTimeDeliveryRate;
    }
    
    public void setOnTimeDeliveryRate(BigDecimal onTimeDeliveryRate) {
        this.onTimeDeliveryRate = onTimeDeliveryRate;
    }
    
    public BigDecimal getQualityRating() {
        return qualityRating;
    }
    
    public void setQualityRating(BigDecimal qualityRating) {
        this.qualityRating = qualityRating;
    }
    
    public Integer getCostCompetitivenessScore() {
        return costCompetitivenessScore;
    }
    
    public void setCostCompetitivenessScore(Integer costCompetitivenessScore) {
        this.costCompetitivenessScore = costCompetitivenessScore;
    }
    
    public Integer getResponsivenessScore() {
        return responsivenessScore;
    }
    
    public void setResponsivenessScore(Integer responsivenessScore) {
        this.responsivenessScore = responsivenessScore;
    }
    
    public List<String> getIsoCertifications() {
        return isoCertifications;
    }
    
    public void setIsoCertifications(List<String> isoCertifications) {
        this.isoCertifications = isoCertifications;
    }
    
    public List<String> getComplianceCertifications() {
        return complianceCertifications;
    }
    
    public void setComplianceCertifications(List<String> complianceCertifications) {
        this.complianceCertifications = complianceCertifications;
    }
    
    public LocalDate getLastAuditDate() {
        return lastAuditDate;
    }
    
    public void setLastAuditDate(LocalDate lastAuditDate) {
        this.lastAuditDate = lastAuditDate;
    }
    
    public LocalDate getNextAuditDueDate() {
        return nextAuditDueDate;
    }
    
    public void setNextAuditDueDate(LocalDate nextAuditDueDate) {
        this.nextAuditDueDate = nextAuditDueDate;
    }
    
    public String getCreditRating() {
        return creditRating;
    }
    
    public void setCreditRating(String creditRating) {
        this.creditRating = creditRating;
    }
    
    public String getPaymentTerms() {
        return paymentTerms;
    }
    
    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public SupplierStatus getStatus() {
        return status;
    }
    
    public void setStatus(SupplierStatus status) {
        this.status = status;
    }
    
    public Boolean getPreferredSupplier() {
        return preferredSupplier;
    }
    
    public void setPreferredSupplier(Boolean preferredSupplier) {
        this.preferredSupplier = preferredSupplier;
    }
    
    public Boolean getStrategicSupplier() {
        return strategicSupplier;
    }
    
    public void setStrategicSupplier(Boolean strategicSupplier) {
        this.strategicSupplier = strategicSupplier;
    }
    
    public User getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    public User getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
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
    
    public Set<SupplierPerformanceHistory> getPerformanceHistory() {
        return performanceHistory;
    }
    
    public void setPerformanceHistory(Set<SupplierPerformanceHistory> performanceHistory) {
        this.performanceHistory = performanceHistory;
    }
    
    public Set<SupplierDocument> getDocuments() {
        return documents;
    }
    
    public void setDocuments(Set<SupplierDocument> documents) {
        this.documents = documents;
    }
    
    public Set<SupplierCategory> getCategories() {
        return categories;
    }
    
    public void setCategories(Set<SupplierCategory> categories) {
        this.categories = categories;
    }
    
    // Enhanced fields getters and setters
    public Double getRiskScore() {
        return riskScore;
    }
    
    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }
    
    public RiskLevel getRiskLevel() {
        return riskLevel;
    }
    
    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }
    
    public LocalDateTime getRiskLastUpdated() {
        return riskLastUpdated;
    }
    
    public void setRiskLastUpdated(LocalDateTime riskLastUpdated) {
        this.riskLastUpdated = riskLastUpdated;
    }
    
    public Supplier getParentSupplier() {
        return parentSupplier;
    }
    
    public void setParentSupplier(Supplier parentSupplier) {
        this.parentSupplier = parentSupplier;
    }
    
    public Set<Supplier> getSubsidiaries() {
        return subsidiaries;
    }
    
    public void setSubsidiaries(Set<Supplier> subsidiaries) {
        this.subsidiaries = subsidiaries;
    }
    
    public Set<SupplierLocation> getLocations() {
        return locations;
    }
    
    public void setLocations(Set<SupplierLocation> locations) {
        this.locations = locations;
    }
    
    public Set<RiskFactor> getRiskFactors() {
        return riskFactors;
    }
    
    public void setRiskFactors(Set<RiskFactor> riskFactors) {
        this.riskFactors = riskFactors;
    }
    
    public Set<SupplierCertification> getCertifications() {
        return certifications;
    }
    
    public void setCertifications(Set<SupplierCertification> certifications) {
        this.certifications = certifications;
    }
}
package com.supplychainrisk.dto;

import com.supplychainrisk.entity.Supplier;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class SupplierDTO {
    
    private Long id;
    
    @NotBlank(message = "Supplier code is required")
    private String supplierCode;
    
    @NotBlank(message = "Supplier name is required")
    private String name;
    
    private String legalName;
    
    private Supplier.SupplierTier tier;
    
    // Contact Information
    private String primaryContactName;
    
    @Email(message = "Invalid email format")
    private String primaryContactEmail;
    
    private String primaryContactPhone;
    private String secondaryContactName;
    
    @Email(message = "Invalid email format")
    private String secondaryContactEmail;
    
    private String secondaryContactPhone;
    private String website;
    
    // Address Information
    private String streetAddress;
    private String city;
    private String stateProvince;
    private String postalCode;
    private String country;
    private BigDecimal latitude;
    private BigDecimal longitude;
    
    // Business Information
    private String industry;
    private String businessType;
    private BigDecimal annualRevenue;
    private Integer employeeCount;
    private Integer yearsInBusiness;
    
    // Risk and Performance Metrics
    @Min(value = 0, message = "Risk score cannot be negative")
    @Max(value = 100, message = "Risk score cannot exceed 100")
    private Integer overallRiskScore;
    
    @Min(value = 0)
    @Max(value = 100)
    private Integer financialRiskScore;
    
    @Min(value = 0)
    @Max(value = 100)
    private Integer operationalRiskScore;
    
    @Min(value = 0)
    @Max(value = 100)
    private Integer complianceRiskScore;
    
    @Min(value = 0)
    @Max(value = 100)
    private Integer geographicRiskScore;
    
    // Performance KPIs
    @DecimalMin(value = "0.00", message = "Delivery rate cannot be negative")
    @DecimalMax(value = "100.00", message = "Delivery rate cannot exceed 100%")
    private BigDecimal onTimeDeliveryRate;
    
    @DecimalMin(value = "0.00")
    @DecimalMax(value = "10.00")
    private BigDecimal qualityRating;
    
    @Min(value = 0)
    @Max(value = 100)
    private Integer costCompetitivenessScore;
    
    @Min(value = 0)
    @Max(value = 100)
    private Integer responsivenessScore;
    
    // Certifications and Compliance
    private List<String> isoCertifications;
    private List<String> complianceCertifications;
    private LocalDate lastAuditDate;
    private LocalDate nextAuditDueDate;
    
    // Financial Information
    private String creditRating;
    private String paymentTerms;
    private String currency;
    
    // Status and Metadata
    private Supplier.SupplierStatus status;
    private Boolean preferredSupplier;
    private Boolean strategicSupplier;
    
    // Audit Fields
    private Long createdById;
    private String createdByName;
    private Long updatedById;
    private String updatedByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Categories
    private List<SupplierCategoryDTO> categories;
    
    // Default constructor
    public SupplierDTO() {}
    
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
    
    public Supplier.SupplierTier getTier() {
        return tier;
    }
    
    public void setTier(Supplier.SupplierTier tier) {
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
    
    public Supplier.SupplierStatus getStatus() {
        return status;
    }
    
    public void setStatus(Supplier.SupplierStatus status) {
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
    
    public Long getCreatedById() {
        return createdById;
    }
    
    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }
    
    public String getCreatedByName() {
        return createdByName;
    }
    
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }
    
    public Long getUpdatedById() {
        return updatedById;
    }
    
    public void setUpdatedById(Long updatedById) {
        this.updatedById = updatedById;
    }
    
    public String getUpdatedByName() {
        return updatedByName;
    }
    
    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
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
    
    public List<SupplierCategoryDTO> getCategories() {
        return categories;
    }
    
    public void setCategories(List<SupplierCategoryDTO> categories) {
        this.categories = categories;
    }
}
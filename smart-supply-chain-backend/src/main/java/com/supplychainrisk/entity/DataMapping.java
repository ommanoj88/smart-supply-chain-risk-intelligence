package com.supplychainrisk.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "data_mappings")
public class DataMapping {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "erp_integration_id")
    private ERPIntegration erpIntegration;
    
    // Source and Target Field Information
    @Column(name = "source_entity", nullable = false, length = 100)
    @NotBlank(message = "Source entity is required")
    private String sourceEntity;
    
    @Column(name = "source_field", nullable = false, length = 100)
    @NotBlank(message = "Source field is required")
    private String sourceField;
    
    @Column(name = "source_field_type", length = 50)
    private String sourceFieldType;
    
    @Column(name = "target_entity", nullable = false, length = 100)
    @NotBlank(message = "Target entity is required")
    private String targetEntity;
    
    @Column(name = "target_field", nullable = false, length = 100)
    @NotBlank(message = "Target field is required")
    private String targetField;
    
    @Column(name = "target_field_type", length = 50)
    private String targetFieldType;
    
    // Transformation Configuration
    @Column(name = "transformation_logic", columnDefinition = "TEXT")
    private String transformationLogic;
    
    @Column(name = "transformation_type", length = 50)
    private String transformationType;
    
    @Column(name = "default_value", length = 255)
    private String defaultValue;
    
    @Column(name = "is_required")
    private Boolean isRequired = false;
    
    @Column(name = "is_key")
    private Boolean isKey = false;
    
    // Validation Rules
    @Column(name = "validation_rules", columnDefinition = "TEXT")
    private String validationRules;
    
    @Column(name = "validation_message", length = 255)
    private String validationMessage;
    
    // Business Logic
    @Column(name = "business_rule_id", length = 100)
    private String businessRuleId;
    
    @Column(name = "conditional_logic", columnDefinition = "TEXT")
    private String conditionalLogic;
    
    // Metadata
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
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
    public DataMapping() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public ERPIntegration getErpIntegration() {
        return erpIntegration;
    }
    
    public void setErpIntegration(ERPIntegration erpIntegration) {
        this.erpIntegration = erpIntegration;
    }
    
    public String getSourceEntity() {
        return sourceEntity;
    }
    
    public void setSourceEntity(String sourceEntity) {
        this.sourceEntity = sourceEntity;
    }
    
    public String getSourceField() {
        return sourceField;
    }
    
    public void setSourceField(String sourceField) {
        this.sourceField = sourceField;
    }
    
    public String getSourceFieldType() {
        return sourceFieldType;
    }
    
    public void setSourceFieldType(String sourceFieldType) {
        this.sourceFieldType = sourceFieldType;
    }
    
    public String getTargetEntity() {
        return targetEntity;
    }
    
    public void setTargetEntity(String targetEntity) {
        this.targetEntity = targetEntity;
    }
    
    public String getTargetField() {
        return targetField;
    }
    
    public void setTargetField(String targetField) {
        this.targetField = targetField;
    }
    
    public String getTargetFieldType() {
        return targetFieldType;
    }
    
    public void setTargetFieldType(String targetFieldType) {
        this.targetFieldType = targetFieldType;
    }
    
    public String getTransformationLogic() {
        return transformationLogic;
    }
    
    public void setTransformationLogic(String transformationLogic) {
        this.transformationLogic = transformationLogic;
    }
    
    public String getTransformationType() {
        return transformationType;
    }
    
    public void setTransformationType(String transformationType) {
        this.transformationType = transformationType;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public Boolean getIsRequired() {
        return isRequired;
    }
    
    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }
    
    public Boolean getIsKey() {
        return isKey;
    }
    
    public void setIsKey(Boolean isKey) {
        this.isKey = isKey;
    }
    
    public String getValidationRules() {
        return validationRules;
    }
    
    public void setValidationRules(String validationRules) {
        this.validationRules = validationRules;
    }
    
    public String getValidationMessage() {
        return validationMessage;
    }
    
    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }
    
    public String getBusinessRuleId() {
        return businessRuleId;
    }
    
    public void setBusinessRuleId(String businessRuleId) {
        this.businessRuleId = businessRuleId;
    }
    
    public String getConditionalLogic() {
        return conditionalLogic;
    }
    
    public void setConditionalLogic(String conditionalLogic) {
        this.conditionalLogic = conditionalLogic;
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
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
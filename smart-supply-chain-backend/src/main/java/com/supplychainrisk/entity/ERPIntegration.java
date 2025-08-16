package com.supplychainrisk.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "erp_integrations")
public class ERPIntegration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Integration Details
    @Column(name = "integration_name", nullable = false, length = 100)
    @NotBlank(message = "Integration name is required")
    private String integrationName;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "erp_system_type", nullable = false)
    private ERPSystemType erpSystemType;
    
    @Column(name = "erp_version", length = 50)
    private String erpVersion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IntegrationStatus status = IntegrationStatus.INACTIVE;
    
    // Connection Configuration
    @Embedded
    private ConnectionConfiguration connectionConfig;
    
    // Authentication Configuration
    @Embedded
    private AuthenticationConfiguration authConfig;
    
    // Data Mapping Configuration
    @OneToMany(mappedBy = "erpIntegration", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DataMapping> dataMappings = new ArrayList<>();
    
    // Synchronization Settings
    @Embedded
    private SynchronizationSettings syncSettings;
    
    // Monitoring Configuration
    @Embedded
    private MonitoringConfiguration monitoringConfig;
    
    // Business Rules
    @OneToMany(mappedBy = "erpIntegration", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BusinessRule> businessRules = new ArrayList<>();
    
    // Integration History
    @OneToMany(mappedBy = "erpIntegration", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IntegrationExecution> executionHistory = new ArrayList<>();
    
    // Performance Metrics
    @OneToMany(mappedBy = "erpIntegration", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IntegrationMetrics> performanceMetrics = new ArrayList<>();
    
    // Metadata
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    @Column(name = "updated_by", length = 100)
    private String updatedBy;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    // Enums
    public enum ERPSystemType {
        SAP, ORACLE, DYNAMICS, GENERIC
    }
    
    public enum IntegrationStatus {
        ACTIVE, INACTIVE, PENDING, ERROR, MAINTENANCE
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
    public ERPIntegration() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getIntegrationName() {
        return integrationName;
    }
    
    public void setIntegrationName(String integrationName) {
        this.integrationName = integrationName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public ERPSystemType getErpSystemType() {
        return erpSystemType;
    }
    
    public void setErpSystemType(ERPSystemType erpSystemType) {
        this.erpSystemType = erpSystemType;
    }
    
    public String getErpVersion() {
        return erpVersion;
    }
    
    public void setErpVersion(String erpVersion) {
        this.erpVersion = erpVersion;
    }
    
    public IntegrationStatus getStatus() {
        return status;
    }
    
    public void setStatus(IntegrationStatus status) {
        this.status = status;
    }
    
    public ConnectionConfiguration getConnectionConfig() {
        return connectionConfig;
    }
    
    public void setConnectionConfig(ConnectionConfiguration connectionConfig) {
        this.connectionConfig = connectionConfig;
    }
    
    public AuthenticationConfiguration getAuthConfig() {
        return authConfig;
    }
    
    public void setAuthConfig(AuthenticationConfiguration authConfig) {
        this.authConfig = authConfig;
    }
    
    public List<DataMapping> getDataMappings() {
        return dataMappings;
    }
    
    public void setDataMappings(List<DataMapping> dataMappings) {
        this.dataMappings = dataMappings;
    }
    
    public SynchronizationSettings getSyncSettings() {
        return syncSettings;
    }
    
    public void setSyncSettings(SynchronizationSettings syncSettings) {
        this.syncSettings = syncSettings;
    }
    
    public MonitoringConfiguration getMonitoringConfig() {
        return monitoringConfig;
    }
    
    public void setMonitoringConfig(MonitoringConfiguration monitoringConfig) {
        this.monitoringConfig = monitoringConfig;
    }
    
    public List<BusinessRule> getBusinessRules() {
        return businessRules;
    }
    
    public void setBusinessRules(List<BusinessRule> businessRules) {
        this.businessRules = businessRules;
    }
    
    public List<IntegrationExecution> getExecutionHistory() {
        return executionHistory;
    }
    
    public void setExecutionHistory(List<IntegrationExecution> executionHistory) {
        this.executionHistory = executionHistory;
    }
    
    public List<IntegrationMetrics> getPerformanceMetrics() {
        return performanceMetrics;
    }
    
    public void setPerformanceMetrics(List<IntegrationMetrics> performanceMetrics) {
        this.performanceMetrics = performanceMetrics;
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
    
    public String getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
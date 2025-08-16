package com.supplychainrisk.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class SynchronizationSettings {
    
    @Enumerated(EnumType.STRING)
    @Column(name = "sync_mode")
    private SyncMode syncMode;
    
    @Column(name = "sync_schedule", length = 100) // Cron expression
    private String syncSchedule;
    
    @Column(name = "batch_size")
    private Integer batchSize;
    
    @Column(name = "enable_delta_sync")
    private Boolean enableDeltaSync;
    
    @Column(name = "delta_field", length = 100)
    private String deltaField;
    
    @Column(name = "enable_real_time")
    private Boolean enableRealTime;
    
    @Column(name = "conflict_resolution_strategy", length = 50)
    private String conflictResolutionStrategy;
    
    @Column(name = "retry_attempts")
    private Integer retryAttempts;
    
    @Column(name = "retry_interval")
    private Integer retryInterval;
    
    public enum SyncMode {
        MANUAL, SCHEDULED, REAL_TIME, HYBRID
    }
    
    // Default constructor
    public SynchronizationSettings() {}
    
    // Getters and Setters
    public SyncMode getSyncMode() {
        return syncMode;
    }
    
    public void setSyncMode(SyncMode syncMode) {
        this.syncMode = syncMode;
    }
    
    public String getSyncSchedule() {
        return syncSchedule;
    }
    
    public void setSyncSchedule(String syncSchedule) {
        this.syncSchedule = syncSchedule;
    }
    
    public Integer getBatchSize() {
        return batchSize;
    }
    
    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }
    
    public Boolean getEnableDeltaSync() {
        return enableDeltaSync;
    }
    
    public void setEnableDeltaSync(Boolean enableDeltaSync) {
        this.enableDeltaSync = enableDeltaSync;
    }
    
    public String getDeltaField() {
        return deltaField;
    }
    
    public void setDeltaField(String deltaField) {
        this.deltaField = deltaField;
    }
    
    public Boolean getEnableRealTime() {
        return enableRealTime;
    }
    
    public void setEnableRealTime(Boolean enableRealTime) {
        this.enableRealTime = enableRealTime;
    }
    
    public String getConflictResolutionStrategy() {
        return conflictResolutionStrategy;
    }
    
    public void setConflictResolutionStrategy(String conflictResolutionStrategy) {
        this.conflictResolutionStrategy = conflictResolutionStrategy;
    }
    
    public Integer getRetryAttempts() {
        return retryAttempts;
    }
    
    public void setRetryAttempts(Integer retryAttempts) {
        this.retryAttempts = retryAttempts;
    }
    
    public Integer getRetryInterval() {
        return retryInterval;
    }
    
    public void setRetryInterval(Integer retryInterval) {
        this.retryInterval = retryInterval;
    }
}
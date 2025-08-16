package com.supplychainrisk.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;

@Embeddable
public class MonitoringConfiguration {
    
    @Column(name = "enable_monitoring")
    private Boolean enableMonitoring;
    
    @Column(name = "health_check_interval")
    private Integer healthCheckInterval;
    
    @Column(name = "performance_metrics_enabled")
    private Boolean performanceMetricsEnabled;
    
    @Column(name = "alert_on_failure")
    private Boolean alertOnFailure;
    
    @Column(name = "alert_threshold_error_rate")
    private Double alertThresholdErrorRate;
    
    @Column(name = "alert_threshold_response_time")
    private Integer alertThresholdResponseTime;
    
    @Column(name = "log_level", length = 20)
    private String logLevel;
    
    @Column(name = "retention_days")
    private Integer retentionDays;
    
    // Default constructor
    public MonitoringConfiguration() {}
    
    // Getters and Setters
    public Boolean getEnableMonitoring() {
        return enableMonitoring;
    }
    
    public void setEnableMonitoring(Boolean enableMonitoring) {
        this.enableMonitoring = enableMonitoring;
    }
    
    public Integer getHealthCheckInterval() {
        return healthCheckInterval;
    }
    
    public void setHealthCheckInterval(Integer healthCheckInterval) {
        this.healthCheckInterval = healthCheckInterval;
    }
    
    public Boolean getPerformanceMetricsEnabled() {
        return performanceMetricsEnabled;
    }
    
    public void setPerformanceMetricsEnabled(Boolean performanceMetricsEnabled) {
        this.performanceMetricsEnabled = performanceMetricsEnabled;
    }
    
    public Boolean getAlertOnFailure() {
        return alertOnFailure;
    }
    
    public void setAlertOnFailure(Boolean alertOnFailure) {
        this.alertOnFailure = alertOnFailure;
    }
    
    public Double getAlertThresholdErrorRate() {
        return alertThresholdErrorRate;
    }
    
    public void setAlertThresholdErrorRate(Double alertThresholdErrorRate) {
        this.alertThresholdErrorRate = alertThresholdErrorRate;
    }
    
    public Integer getAlertThresholdResponseTime() {
        return alertThresholdResponseTime;
    }
    
    public void setAlertThresholdResponseTime(Integer alertThresholdResponseTime) {
        this.alertThresholdResponseTime = alertThresholdResponseTime;
    }
    
    public String getLogLevel() {
        return logLevel;
    }
    
    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }
    
    public Integer getRetentionDays() {
        return retentionDays;
    }
    
    public void setRetentionDays(Integer retentionDays) {
        this.retentionDays = retentionDays;
    }
}
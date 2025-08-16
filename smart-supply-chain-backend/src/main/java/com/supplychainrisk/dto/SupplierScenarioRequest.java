package com.supplychainrisk.dto;

import java.time.Duration;
import java.util.Map;

/**
 * DTO for supplier scenario generation requests
 */
public class SupplierScenarioRequest {
    private Long supplierId;
    private String scenarioType;
    private String severity;
    private Duration timeframe;
    private Map<String, Object> parameters;
    
    // Default constructor
    public SupplierScenarioRequest() {}
    
    // Getters and setters
    public Long getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }
    
    public String getScenarioType() {
        return scenarioType;
    }
    
    public void setScenarioType(String scenarioType) {
        this.scenarioType = scenarioType;
    }
    
    public String getSeverity() {
        return severity;
    }
    
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    
    public Duration getTimeframe() {
        return timeframe;
    }
    
    public void setTimeframe(Duration timeframe) {
        this.timeframe = timeframe;
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
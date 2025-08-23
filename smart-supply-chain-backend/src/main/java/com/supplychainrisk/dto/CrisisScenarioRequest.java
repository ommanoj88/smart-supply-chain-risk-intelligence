package com.supplychainrisk.dto;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * DTO for crisis scenario generation requests
 */
public class CrisisScenarioRequest {
    private String scenarioType;
    private List<String> affectedRegions;
    private String severity;
    private Duration duration;
    private Map<String, Object> parameters;
    
    // Default constructor
    public CrisisScenarioRequest() {}
    
    // Getters and setters
    public String getScenarioType() {
        return scenarioType;
    }
    
    public void setScenarioType(String scenarioType) {
        this.scenarioType = scenarioType;
    }
    
    public List<String> getAffectedRegions() {
        return affectedRegions;
    }
    
    public void setAffectedRegions(List<String> affectedRegions) {
        this.affectedRegions = affectedRegions;
    }
    
    public String getSeverity() {
        return severity;
    }
    
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    
    public Duration getDuration() {
        return duration;
    }
    
    public void setDuration(Duration duration) {
        this.duration = duration;
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
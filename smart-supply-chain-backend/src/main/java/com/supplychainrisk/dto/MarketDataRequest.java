package com.supplychainrisk.dto;

import java.time.Duration;
import java.util.Map;

/**
 * DTO for market data scenario generation requests
 */
public class MarketDataRequest {
    private String dataType;
    private String region;
    private Duration timeRange;
    private Double volatilityLevel;
    private Map<String, Object> parameters;
    
    // Default constructor
    public MarketDataRequest() {}
    
    // Getters and setters
    public String getDataType() {
        return dataType;
    }
    
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public Duration getTimeRange() {
        return timeRange;
    }
    
    public void setTimeRange(Duration timeRange) {
        this.timeRange = timeRange;
    }
    
    public Double getVolatilityLevel() {
        return volatilityLevel;
    }
    
    public void setVolatilityLevel(Double volatilityLevel) {
        this.volatilityLevel = volatilityLevel;
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
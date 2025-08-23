package com.supplychainrisk.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class MLPredictionRequest {
    
    private String predictionType;
    private List<Map<String, Object>> inputData;
    private String modelId;
    private String modelVersion;
    private Integer timeHorizonDays;
    private Map<String, Object> parameters;
    private String requestId;
    private String requestedBy;
    private LocalDateTime requestTime;
    
    // Feature configuration
    private List<String> requiredFeatures;
    private Map<String, Object> featureConfig;
    private Boolean enableFeatureEngineering;
    
    // Output configuration
    private Boolean includeConfidenceIntervals;
    private Boolean includeFeatureImportance;
    private Boolean includeExplanations;
    private String outputFormat;
    
    // Quality and performance settings
    private Integer maxLatencyMs;
    private java.math.BigDecimal minConfidenceThreshold;
    private Boolean enableCaching;
    private String priority;
    
    public enum PredictionType {
        RISK_ASSESSMENT,
        DELAY_PREDICTION,
        COST_FORECAST,
        DEMAND_FORECAST,
        ANOMALY_DETECTION,
        CLASSIFICATION,
        REGRESSION,
        TIME_SERIES
    }
    
    // Default constructor
    public MLPredictionRequest() {
        this.requestTime = LocalDateTime.now();
        this.requestId = java.util.UUID.randomUUID().toString();
    }
    
    // Builder pattern
    public static class Builder {
        private MLPredictionRequest request = new MLPredictionRequest();
        
        public Builder predictionType(String predictionType) {
            request.predictionType = predictionType;
            return this;
        }
        
        public Builder inputData(List<Map<String, Object>> inputData) {
            request.inputData = inputData;
            return this;
        }
        
        public Builder modelId(String modelId) {
            request.modelId = modelId;
            return this;
        }
        
        public Builder modelVersion(String modelVersion) {
            request.modelVersion = modelVersion;
            return this;
        }
        
        public Builder timeHorizonDays(Integer timeHorizonDays) {
            request.timeHorizonDays = timeHorizonDays;
            return this;
        }
        
        public Builder parameters(Map<String, Object> parameters) {
            request.parameters = parameters;
            return this;
        }
        
        public Builder requestId(String requestId) {
            request.requestId = requestId;
            return this;
        }
        
        public Builder requestedBy(String requestedBy) {
            request.requestedBy = requestedBy;
            return this;
        }
        
        public Builder requiredFeatures(List<String> requiredFeatures) {
            request.requiredFeatures = requiredFeatures;
            return this;
        }
        
        public Builder featureConfig(Map<String, Object> featureConfig) {
            request.featureConfig = featureConfig;
            return this;
        }
        
        public Builder enableFeatureEngineering(Boolean enableFeatureEngineering) {
            request.enableFeatureEngineering = enableFeatureEngineering;
            return this;
        }
        
        public Builder includeConfidenceIntervals(Boolean includeConfidenceIntervals) {
            request.includeConfidenceIntervals = includeConfidenceIntervals;
            return this;
        }
        
        public Builder includeFeatureImportance(Boolean includeFeatureImportance) {
            request.includeFeatureImportance = includeFeatureImportance;
            return this;
        }
        
        public Builder includeExplanations(Boolean includeExplanations) {
            request.includeExplanations = includeExplanations;
            return this;
        }
        
        public Builder outputFormat(String outputFormat) {
            request.outputFormat = outputFormat;
            return this;
        }
        
        public Builder maxLatencyMs(Integer maxLatencyMs) {
            request.maxLatencyMs = maxLatencyMs;
            return this;
        }
        
        public Builder minConfidenceThreshold(java.math.BigDecimal minConfidenceThreshold) {
            request.minConfidenceThreshold = minConfidenceThreshold;
            return this;
        }
        
        public Builder enableCaching(Boolean enableCaching) {
            request.enableCaching = enableCaching;
            return this;
        }
        
        public Builder priority(String priority) {
            request.priority = priority;
            return this;
        }
        
        public MLPredictionRequest build() {
            return request;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Getters and Setters
    public String getPredictionType() {
        return predictionType;
    }
    
    public void setPredictionType(String predictionType) {
        this.predictionType = predictionType;
    }
    
    public List<Map<String, Object>> getInputData() {
        return inputData;
    }
    
    public void setInputData(List<Map<String, Object>> inputData) {
        this.inputData = inputData;
    }
    
    public String getModelId() {
        return modelId;
    }
    
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }
    
    public String getModelVersion() {
        return modelVersion;
    }
    
    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }
    
    public Integer getTimeHorizonDays() {
        return timeHorizonDays;
    }
    
    public void setTimeHorizonDays(Integer timeHorizonDays) {
        this.timeHorizonDays = timeHorizonDays;
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public String getRequestedBy() {
        return requestedBy;
    }
    
    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }
    
    public LocalDateTime getRequestTime() {
        return requestTime;
    }
    
    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }
    
    public List<String> getRequiredFeatures() {
        return requiredFeatures;
    }
    
    public void setRequiredFeatures(List<String> requiredFeatures) {
        this.requiredFeatures = requiredFeatures;
    }
    
    public Map<String, Object> getFeatureConfig() {
        return featureConfig;
    }
    
    public void setFeatureConfig(Map<String, Object> featureConfig) {
        this.featureConfig = featureConfig;
    }
    
    public Boolean getEnableFeatureEngineering() {
        return enableFeatureEngineering;
    }
    
    public void setEnableFeatureEngineering(Boolean enableFeatureEngineering) {
        this.enableFeatureEngineering = enableFeatureEngineering;
    }
    
    public Boolean getIncludeConfidenceIntervals() {
        return includeConfidenceIntervals;
    }
    
    public void setIncludeConfidenceIntervals(Boolean includeConfidenceIntervals) {
        this.includeConfidenceIntervals = includeConfidenceIntervals;
    }
    
    public Boolean getIncludeFeatureImportance() {
        return includeFeatureImportance;
    }
    
    public void setIncludeFeatureImportance(Boolean includeFeatureImportance) {
        this.includeFeatureImportance = includeFeatureImportance;
    }
    
    public Boolean getIncludeExplanations() {
        return includeExplanations;
    }
    
    public void setIncludeExplanations(Boolean includeExplanations) {
        this.includeExplanations = includeExplanations;
    }
    
    public String getOutputFormat() {
        return outputFormat;
    }
    
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }
    
    public Integer getMaxLatencyMs() {
        return maxLatencyMs;
    }
    
    public void setMaxLatencyMs(Integer maxLatencyMs) {
        this.maxLatencyMs = maxLatencyMs;
    }
    
    public java.math.BigDecimal getMinConfidenceThreshold() {
        return minConfidenceThreshold;
    }
    
    public void setMinConfidenceThreshold(java.math.BigDecimal minConfidenceThreshold) {
        this.minConfidenceThreshold = minConfidenceThreshold;
    }
    
    public Boolean getEnableCaching() {
        return enableCaching;
    }
    
    public void setEnableCaching(Boolean enableCaching) {
        this.enableCaching = enableCaching;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
}
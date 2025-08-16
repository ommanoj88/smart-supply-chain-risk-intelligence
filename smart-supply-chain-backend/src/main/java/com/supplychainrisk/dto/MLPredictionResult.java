package com.supplychainrisk.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class MLPredictionResult {
    private Map<String, Object> predictions;
    private BigDecimal confidence;
    private String modelVersion;
    private LocalDateTime generatedAt;
    private Map<String, Object> features;
    private Map<String, BigDecimal> uncertaintyBounds;
    
    // Default constructor
    public MLPredictionResult() {}
    
    public static class Builder {
        private MLPredictionResult result = new MLPredictionResult();
        
        public Builder predictions(Map<String, Object> predictions) {
            result.predictions = predictions;
            return this;
        }
        
        public Builder confidence(BigDecimal confidence) {
            result.confidence = confidence;
            return this;
        }
        
        public Builder modelVersion(String modelVersion) {
            result.modelVersion = modelVersion;
            return this;
        }
        
        public Builder generatedAt(LocalDateTime generatedAt) {
            result.generatedAt = generatedAt;
            return this;
        }
        
        public Builder features(Map<String, Object> features) {
            result.features = features;
            return this;
        }
        
        public Builder uncertaintyBounds(Map<String, BigDecimal> uncertaintyBounds) {
            result.uncertaintyBounds = uncertaintyBounds;
            return this;
        }
        
        public MLPredictionResult build() {
            return result;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Getters and setters
    public Map<String, Object> getPredictions() {
        return predictions;
    }
    
    public void setPredictions(Map<String, Object> predictions) {
        this.predictions = predictions;
    }
    
    public BigDecimal getConfidence() {
        return confidence;
    }
    
    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }
    
    public String getModelVersion() {
        return modelVersion;
    }
    
    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }
    
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
    
    public Map<String, Object> getFeatures() {
        return features;
    }
    
    public void setFeatures(Map<String, Object> features) {
        this.features = features;
    }
    
    public Map<String, BigDecimal> getUncertaintyBounds() {
        return uncertaintyBounds;
    }
    
    public void setUncertaintyBounds(Map<String, BigDecimal> uncertaintyBounds) {
        this.uncertaintyBounds = uncertaintyBounds;
    }
}
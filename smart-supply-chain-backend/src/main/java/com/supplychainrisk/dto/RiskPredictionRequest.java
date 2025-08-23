package com.supplychainrisk.dto;

import com.supplychainrisk.entity.RiskPrediction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class RiskPredictionRequest {
    
    private List<Long> supplierIds;
    private List<Long> shipmentIds;
    private List<RiskPrediction.RiskType> riskTypes;
    private Integer predictionHorizonDays;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String modelId;
    private BigDecimal confidenceThreshold;
    private Map<String, Object> contextData;
    
    // Analysis flags
    private Boolean includeDelayPredictions = true;
    private Boolean includeCostPredictions = true;
    private Boolean includeDisruptionPredictions = true;
    private Boolean includeDemandForecasts = true;
    private Boolean includeRiskEventPredictions = true;
    
    // Business context
    private String requestedBy;
    private String businessUnit;
    private String priority;
    private Map<String, Object> businessContext;
    
    // Default constructor
    public RiskPredictionRequest() {}
    
    // Builder pattern
    public static class Builder {
        private RiskPredictionRequest request = new RiskPredictionRequest();
        
        public Builder supplierIds(List<Long> supplierIds) {
            request.supplierIds = supplierIds;
            return this;
        }
        
        public Builder shipmentIds(List<Long> shipmentIds) {
            request.shipmentIds = shipmentIds;
            return this;
        }
        
        public Builder riskTypes(List<RiskPrediction.RiskType> riskTypes) {
            request.riskTypes = riskTypes;
            return this;
        }
        
        public Builder predictionHorizonDays(Integer predictionHorizonDays) {
            request.predictionHorizonDays = predictionHorizonDays;
            return this;
        }
        
        public Builder dateRange(LocalDateTime startDate, LocalDateTime endDate) {
            request.startDate = startDate;
            request.endDate = endDate;
            return this;
        }
        
        public Builder modelId(String modelId) {
            request.modelId = modelId;
            return this;
        }
        
        public Builder confidenceThreshold(BigDecimal confidenceThreshold) {
            request.confidenceThreshold = confidenceThreshold;
            return this;
        }
        
        public Builder contextData(Map<String, Object> contextData) {
            request.contextData = contextData;
            return this;
        }
        
        public Builder includeDelayPredictions(Boolean includeDelayPredictions) {
            request.includeDelayPredictions = includeDelayPredictions;
            return this;
        }
        
        public Builder includeCostPredictions(Boolean includeCostPredictions) {
            request.includeCostPredictions = includeCostPredictions;
            return this;
        }
        
        public Builder includeDisruptionPredictions(Boolean includeDisruptionPredictions) {
            request.includeDisruptionPredictions = includeDisruptionPredictions;
            return this;
        }
        
        public Builder includeDemandForecasts(Boolean includeDemandForecasts) {
            request.includeDemandForecasts = includeDemandForecasts;
            return this;
        }
        
        public Builder includeRiskEventPredictions(Boolean includeRiskEventPredictions) {
            request.includeRiskEventPredictions = includeRiskEventPredictions;
            return this;
        }
        
        public Builder requestedBy(String requestedBy) {
            request.requestedBy = requestedBy;
            return this;
        }
        
        public Builder businessUnit(String businessUnit) {
            request.businessUnit = businessUnit;
            return this;
        }
        
        public Builder priority(String priority) {
            request.priority = priority;
            return this;
        }
        
        public Builder businessContext(Map<String, Object> businessContext) {
            request.businessContext = businessContext;
            return this;
        }
        
        public RiskPredictionRequest build() {
            return request;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Getters and Setters
    public List<Long> getSupplierIds() {
        return supplierIds;
    }
    
    public void setSupplierIds(List<Long> supplierIds) {
        this.supplierIds = supplierIds;
    }
    
    public List<Long> getShipmentIds() {
        return shipmentIds;
    }
    
    public void setShipmentIds(List<Long> shipmentIds) {
        this.shipmentIds = shipmentIds;
    }
    
    public List<RiskPrediction.RiskType> getRiskTypes() {
        return riskTypes;
    }
    
    public void setRiskTypes(List<RiskPrediction.RiskType> riskTypes) {
        this.riskTypes = riskTypes;
    }
    
    public Integer getPredictionHorizonDays() {
        return predictionHorizonDays;
    }
    
    public void setPredictionHorizonDays(Integer predictionHorizonDays) {
        this.predictionHorizonDays = predictionHorizonDays;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public String getModelId() {
        return modelId;
    }
    
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }
    
    public BigDecimal getConfidenceThreshold() {
        return confidenceThreshold;
    }
    
    public void setConfidenceThreshold(BigDecimal confidenceThreshold) {
        this.confidenceThreshold = confidenceThreshold;
    }
    
    public Map<String, Object> getContextData() {
        return contextData;
    }
    
    public void setContextData(Map<String, Object> contextData) {
        this.contextData = contextData;
    }
    
    public Boolean getIncludeDelayPredictions() {
        return includeDelayPredictions;
    }
    
    public void setIncludeDelayPredictions(Boolean includeDelayPredictions) {
        this.includeDelayPredictions = includeDelayPredictions;
    }
    
    public Boolean getIncludeCostPredictions() {
        return includeCostPredictions;
    }
    
    public void setIncludeCostPredictions(Boolean includeCostPredictions) {
        this.includeCostPredictions = includeCostPredictions;
    }
    
    public Boolean getIncludeDisruptionPredictions() {
        return includeDisruptionPredictions;
    }
    
    public void setIncludeDisruptionPredictions(Boolean includeDisruptionPredictions) {
        this.includeDisruptionPredictions = includeDisruptionPredictions;
    }
    
    public Boolean getIncludeDemandForecasts() {
        return includeDemandForecasts;
    }
    
    public void setIncludeDemandForecasts(Boolean includeDemandForecasts) {
        this.includeDemandForecasts = includeDemandForecasts;
    }
    
    public Boolean getIncludeRiskEventPredictions() {
        return includeRiskEventPredictions;
    }
    
    public void setIncludeRiskEventPredictions(Boolean includeRiskEventPredictions) {
        this.includeRiskEventPredictions = includeRiskEventPredictions;
    }
    
    public String getRequestedBy() {
        return requestedBy;
    }
    
    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }
    
    public String getBusinessUnit() {
        return businessUnit;
    }
    
    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public Map<String, Object> getBusinessContext() {
        return businessContext;
    }
    
    public void setBusinessContext(Map<String, Object> businessContext) {
        this.businessContext = businessContext;
    }
}
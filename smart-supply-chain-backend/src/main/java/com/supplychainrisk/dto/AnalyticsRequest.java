package com.supplychainrisk.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class AnalyticsRequest {
    
    private String analysisType;
    private Integer timeHorizonDays;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<Long> supplierIds;
    private Map<String, Object> filters;
    private String predictionModel;
    private Map<String, Object> businessContext;
    
    // Default constructor
    public AnalyticsRequest() {}
    
    // Builder pattern
    public static class Builder {
        private AnalyticsRequest request = new AnalyticsRequest();
        
        public Builder analysisType(String analysisType) {
            request.analysisType = analysisType;
            return this;
        }
        
        public Builder timeHorizonDays(Integer timeHorizonDays) {
            request.timeHorizonDays = timeHorizonDays;
            return this;
        }
        
        public Builder dateRange(LocalDateTime startDate, LocalDateTime endDate) {
            request.startDate = startDate;
            request.endDate = endDate;
            return this;
        }
        
        public Builder supplierIds(List<Long> supplierIds) {
            request.supplierIds = supplierIds;
            return this;
        }
        
        public Builder filters(Map<String, Object> filters) {
            request.filters = filters;
            return this;
        }
        
        public Builder predictionModel(String predictionModel) {
            request.predictionModel = predictionModel;
            return this;
        }
        
        public Builder businessContext(Map<String, Object> businessContext) {
            request.businessContext = businessContext;
            return this;
        }
        
        public AnalyticsRequest build() {
            return request;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Getters and setters
    public String getAnalysisType() {
        return analysisType;
    }
    
    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }
    
    public Integer getTimeHorizonDays() {
        return timeHorizonDays;
    }
    
    public void setTimeHorizonDays(Integer timeHorizonDays) {
        this.timeHorizonDays = timeHorizonDays;
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
    
    public List<Long> getSupplierIds() {
        return supplierIds;
    }
    
    public void setSupplierIds(List<Long> supplierIds) {
        this.supplierIds = supplierIds;
    }
    
    public Map<String, Object> getFilters() {
        return filters;
    }
    
    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }
    
    public String getPredictionModel() {
        return predictionModel;
    }
    
    public void setPredictionModel(String predictionModel) {
        this.predictionModel = predictionModel;
    }
    
    public Map<String, Object> getBusinessContext() {
        return businessContext;
    }
    
    public void setBusinessContext(Map<String, Object> businessContext) {
        this.businessContext = businessContext;
    }
}
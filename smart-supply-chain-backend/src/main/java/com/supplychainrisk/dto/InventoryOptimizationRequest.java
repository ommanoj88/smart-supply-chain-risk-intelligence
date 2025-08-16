package com.supplychainrisk.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class InventoryOptimizationRequest {
    
    private Long productId;
    private String productCode;
    private String productName;
    private List<Long> supplierIds;
    private DateRange timeRange;
    private Map<String, Object> constraints;
    private String optimizationType; // MIN_COST, MIN_RISK, BALANCED
    private BigDecimal maxBudget;
    private Integer maxLeadTimeDays;
    private BigDecimal currentInventoryLevel;
    private BigDecimal minStockLevel;
    private BigDecimal maxStockLevel;
    private Map<String, Object> demandForecasts;
    
    // Default constructor
    public InventoryOptimizationRequest() {}
    
    // Nested class for date range
    public static class DateRange {
        private LocalDate startDate;
        private LocalDate endDate;
        
        public DateRange() {}
        
        public DateRange(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
        
        public LocalDate getStartDate() {
            return startDate;
        }
        
        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }
        
        public LocalDate getEndDate() {
            return endDate;
        }
        
        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }
    }
    
    // Getters and setters
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public String getProductCode() {
        return productCode;
    }
    
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public List<Long> getSupplierIds() {
        return supplierIds;
    }
    
    public void setSupplierIds(List<Long> supplierIds) {
        this.supplierIds = supplierIds;
    }
    
    public DateRange getTimeRange() {
        return timeRange;
    }
    
    public void setTimeRange(DateRange timeRange) {
        this.timeRange = timeRange;
    }
    
    public Map<String, Object> getConstraints() {
        return constraints;
    }
    
    public void setConstraints(Map<String, Object> constraints) {
        this.constraints = constraints;
    }
    
    public String getOptimizationType() {
        return optimizationType;
    }
    
    public void setOptimizationType(String optimizationType) {
        this.optimizationType = optimizationType;
    }
    
    public BigDecimal getMaxBudget() {
        return maxBudget;
    }
    
    public void setMaxBudget(BigDecimal maxBudget) {
        this.maxBudget = maxBudget;
    }
    
    public Integer getMaxLeadTimeDays() {
        return maxLeadTimeDays;
    }
    
    public void setMaxLeadTimeDays(Integer maxLeadTimeDays) {
        this.maxLeadTimeDays = maxLeadTimeDays;
    }
    
    public BigDecimal getCurrentInventoryLevel() {
        return currentInventoryLevel;
    }
    
    public void setCurrentInventoryLevel(BigDecimal currentInventoryLevel) {
        this.currentInventoryLevel = currentInventoryLevel;
    }
    
    public BigDecimal getMinStockLevel() {
        return minStockLevel;
    }
    
    public void setMinStockLevel(BigDecimal minStockLevel) {
        this.minStockLevel = minStockLevel;
    }
    
    public BigDecimal getMaxStockLevel() {
        return maxStockLevel;
    }
    
    public void setMaxStockLevel(BigDecimal maxStockLevel) {
        this.maxStockLevel = maxStockLevel;
    }
    
    public Map<String, Object> getDemandForecasts() {
        return demandForecasts;
    }
    
    public void setDemandForecasts(Map<String, Object> demandForecasts) {
        this.demandForecasts = demandForecasts;
    }
}
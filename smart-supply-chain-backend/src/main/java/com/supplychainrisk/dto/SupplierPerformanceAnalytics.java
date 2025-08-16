package com.supplychainrisk.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class SupplierPerformanceAnalytics {
    
    private Long supplierId;
    private String supplierName;
    private PerformanceMetrics currentMetrics;
    private List<PerformanceTrend> trends;
    private BenchmarkData benchmarks;
    private List<ImprovementRecommendation> recommendations;
    private TimeRange timeRange;
    private LocalDateTime generatedAt;
    
    // Default constructor
    public SupplierPerformanceAnalytics() {
        this.generatedAt = LocalDateTime.now();
    }
    
    // Inner classes
    public static class PerformanceMetrics {
        private Double onTimeDeliveryRate;
        private Double qualityScore;
        private Double costCompetitiveness;
        private Double responsiveness;
        private Double innovation;
        private Double sustainability;
        private LocalDateTime lastCalculated;
        
        // Getters and Setters
        public Double getOnTimeDeliveryRate() { return onTimeDeliveryRate; }
        public void setOnTimeDeliveryRate(Double onTimeDeliveryRate) { this.onTimeDeliveryRate = onTimeDeliveryRate; }
        
        public Double getQualityScore() { return qualityScore; }
        public void setQualityScore(Double qualityScore) { this.qualityScore = qualityScore; }
        
        public Double getCostCompetitiveness() { return costCompetitiveness; }
        public void setCostCompetitiveness(Double costCompetitiveness) { this.costCompetitiveness = costCompetitiveness; }
        
        public Double getResponsiveness() { return responsiveness; }
        public void setResponsiveness(Double responsiveness) { this.responsiveness = responsiveness; }
        
        public Double getInnovation() { return innovation; }
        public void setInnovation(Double innovation) { this.innovation = innovation; }
        
        public Double getSustainability() { return sustainability; }
        public void setSustainability(Double sustainability) { this.sustainability = sustainability; }
        
        public LocalDateTime getLastCalculated() { return lastCalculated; }
        public void setLastCalculated(LocalDateTime lastCalculated) { this.lastCalculated = lastCalculated; }
    }
    
    public static class PerformanceTrend {
        private String metric;
        private List<TrendDataPoint> dataPoints;
        private String trendDirection; // IMPROVING, DECLINING, STABLE
        private Double changePercentage;
        
        // Getters and Setters
        public String getMetric() { return metric; }
        public void setMetric(String metric) { this.metric = metric; }
        
        public List<TrendDataPoint> getDataPoints() { return dataPoints; }
        public void setDataPoints(List<TrendDataPoint> dataPoints) { this.dataPoints = dataPoints; }
        
        public String getTrendDirection() { return trendDirection; }
        public void setTrendDirection(String trendDirection) { this.trendDirection = trendDirection; }
        
        public Double getChangePercentage() { return changePercentage; }
        public void setChangePercentage(Double changePercentage) { this.changePercentage = changePercentage; }
    }
    
    public static class TrendDataPoint {
        private LocalDateTime timestamp;
        private Double value;
        
        public TrendDataPoint() {}
        
        public TrendDataPoint(LocalDateTime timestamp, Double value) {
            this.timestamp = timestamp;
            this.value = value;
        }
        
        // Getters and Setters
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public Double getValue() { return value; }
        public void setValue(Double value) { this.value = value; }
    }
    
    public static class BenchmarkData {
        private Map<String, Double> industryAverages;
        private Map<String, Double> topPerformers;
        private Map<String, String> ranking; // Position in industry
        
        // Getters and Setters
        public Map<String, Double> getIndustryAverages() { return industryAverages; }
        public void setIndustryAverages(Map<String, Double> industryAverages) { this.industryAverages = industryAverages; }
        
        public Map<String, Double> getTopPerformers() { return topPerformers; }
        public void setTopPerformers(Map<String, Double> topPerformers) { this.topPerformers = topPerformers; }
        
        public Map<String, String> getRanking() { return ranking; }
        public void setRanking(Map<String, String> ranking) { this.ranking = ranking; }
    }
    
    public static class ImprovementRecommendation {
        private String category;
        private String recommendation;
        private String priority; // HIGH, MEDIUM, LOW
        private String expectedImpact;
        private Integer estimatedTimeframe; // in months
        
        // Getters and Setters
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
        
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        
        public String getExpectedImpact() { return expectedImpact; }
        public void setExpectedImpact(String expectedImpact) { this.expectedImpact = expectedImpact; }
        
        public Integer getEstimatedTimeframe() { return estimatedTimeframe; }
        public void setEstimatedTimeframe(Integer estimatedTimeframe) { this.estimatedTimeframe = estimatedTimeframe; }
    }
    
    public enum TimeRange {
        LAST_MONTH, LAST_3_MONTHS, LAST_6_MONTHS, LAST_12_MONTHS, LAST_2_YEARS
    }
    
    // Getters and Setters
    public Long getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }
    
    public String getSupplierName() {
        return supplierName;
    }
    
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    
    public PerformanceMetrics getCurrentMetrics() {
        return currentMetrics;
    }
    
    public void setCurrentMetrics(PerformanceMetrics currentMetrics) {
        this.currentMetrics = currentMetrics;
    }
    
    public List<PerformanceTrend> getTrends() {
        return trends;
    }
    
    public void setTrends(List<PerformanceTrend> trends) {
        this.trends = trends;
    }
    
    public BenchmarkData getBenchmarks() {
        return benchmarks;
    }
    
    public void setBenchmarks(BenchmarkData benchmarks) {
        this.benchmarks = benchmarks;
    }
    
    public List<ImprovementRecommendation> getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(List<ImprovementRecommendation> recommendations) {
        this.recommendations = recommendations;
    }
    
    public TimeRange getTimeRange() {
        return timeRange;
    }
    
    public void setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
    }
    
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}
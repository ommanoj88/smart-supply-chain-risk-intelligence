package com.supplychainrisk.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "integration_metrics")
public class IntegrationMetrics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "erp_integration_id")
    private ERPIntegration erpIntegration;
    
    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;
    
    @Column(name = "metric_value")
    private Double metricValue;
    
    @Column(name = "metric_unit", length = 50)
    private String metricUnit;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "metric_type", nullable = false)
    private MetricType metricType;
    
    @Column(name = "measurement_time")
    private LocalDateTime measurementTime;
    
    @Column(name = "aggregation_period", length = 50)
    private String aggregationPeriod;
    
    @Column(name = "tags", columnDefinition = "TEXT") // JSON format
    private String tags;
    
    public enum MetricType {
        PERFORMANCE, THROUGHPUT, ERROR_RATE, LATENCY, AVAILABILITY, CUSTOM
    }
    
    @PrePersist
    protected void onCreate() {
        if (measurementTime == null) {
            measurementTime = LocalDateTime.now();
        }
    }
    
    // Default constructor
    public IntegrationMetrics() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public ERPIntegration getErpIntegration() {
        return erpIntegration;
    }
    
    public void setErpIntegration(ERPIntegration erpIntegration) {
        this.erpIntegration = erpIntegration;
    }
    
    public String getMetricName() {
        return metricName;
    }
    
    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }
    
    public Double getMetricValue() {
        return metricValue;
    }
    
    public void setMetricValue(Double metricValue) {
        this.metricValue = metricValue;
    }
    
    public String getMetricUnit() {
        return metricUnit;
    }
    
    public void setMetricUnit(String metricUnit) {
        this.metricUnit = metricUnit;
    }
    
    public MetricType getMetricType() {
        return metricType;
    }
    
    public void setMetricType(MetricType metricType) {
        this.metricType = metricType;
    }
    
    public LocalDateTime getMeasurementTime() {
        return measurementTime;
    }
    
    public void setMeasurementTime(LocalDateTime measurementTime) {
        this.measurementTime = measurementTime;
    }
    
    public String getAggregationPeriod() {
        return aggregationPeriod;
    }
    
    public void setAggregationPeriod(String aggregationPeriod) {
        this.aggregationPeriod = aggregationPeriod;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
}
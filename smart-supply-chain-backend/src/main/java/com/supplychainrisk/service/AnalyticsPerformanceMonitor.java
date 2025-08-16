package com.supplychainrisk.service;

import com.supplychainrisk.dto.MLPredictionResult;
import com.supplychainrisk.service.EnhancedWebSocketService.AnalyticsAlertEvent;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class AnalyticsPerformanceMonitor implements HealthIndicator {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsPerformanceMonitor.class);
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    @Autowired
    private MLPredictionService mlPredictionService;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    // Performance tracking variables
    private final AtomicInteger totalRequests = new AtomicInteger(0);
    private final AtomicInteger successfulRequests = new AtomicInteger(0);
    private final AtomicInteger failedRequests = new AtomicInteger(0);
    private final AtomicLong totalProcessingTime = new AtomicLong(0);
    
    // ML Model metrics
    private final Map<String, MLModelMetrics> modelMetrics = new ConcurrentHashMap<>();
    
    // System health metrics
    private double lastCpuUsage = 0.0;
    private double lastMemoryUsage = 0.0;
    private int lastActiveConnections = 0;
    private double lastCacheHitRatio = 0.95;
    
    /**
     * Handle analytics request events for performance tracking
     */
    @EventListener
    public void handleAnalyticsRequest(AnalyticsRequestEvent event) {
        totalRequests.incrementAndGet();
        
        // Track request metrics
        meterRegistry.counter("analytics.requests.total").increment();
        
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            // Process analytics request
            processAnalyticsRequest(event.getRequest());
            
            successfulRequests.incrementAndGet();
            meterRegistry.counter("analytics.requests.success").increment();
            
        } catch (Exception e) {
            failedRequests.incrementAndGet();
            meterRegistry.counter("analytics.requests.error").increment();
            logger.error("Analytics request failed", e);
            
            // Publish alert for failed request
            publishAlert("REQUEST_FAILURE", "HIGH", 
                "Analytics request failed: " + e.getMessage(), 
                Map.of("error", e.getMessage(), "timestamp", LocalDateTime.now()));
            
        } finally {
            long duration = sample.stop(Timer.builder("analytics.request.duration")
                .register(meterRegistry));
            
            totalProcessingTime.addAndGet(duration);
        }
    }
    
    /**
     * Monitor ML model performance every minute
     */
    @Scheduled(fixedRate = 60000) // Every minute
    public void monitorMLModelPerformance() {
        try {
            logger.debug("Monitoring ML model performance");
            
            // Check ML model accuracy and performance
            MLModelMetrics metrics = collectMLModelMetrics();
            modelMetrics.put("primary", metrics);
            
            // Update Micrometer metrics
            meterRegistry.gauge("ml.model.accuracy", metrics.getAccuracy());
            meterRegistry.gauge("ml.model.precision", metrics.getPrecision());
            meterRegistry.gauge("ml.model.recall", metrics.getRecall());
            meterRegistry.gauge("ml.model.latency", metrics.getAverageLatency());
            meterRegistry.gauge("ml.model.requests.total", metrics.getTotalRequests());
            
            // Alert if performance degrades
            if (metrics.getAccuracy() < 0.85) {
                publishAlert("ML_MODEL_DEGRADATION", "HIGH",
                    "ML model accuracy below threshold: " + String.format("%.2f%%", metrics.getAccuracy() * 100),
                    Map.of("modelMetrics", metrics, "threshold", 0.85));
            }
            
            // Monitor prediction latency
            if (metrics.getAverageLatency() > 5000) { // 5 seconds
                publishAlert("ML_SERVICE_SLOW", "MEDIUM",
                    "ML service latency too high: " + metrics.getAverageLatency() + "ms",
                    Map.of("latency", metrics.getAverageLatency(), "threshold", 5000));
            }
            
            // Check for model drift
            if (metrics.getDriftScore() > 0.3) {
                publishAlert("MODEL_DRIFT_DETECTED", "MEDIUM",
                    "Model drift detected, consider retraining",
                    Map.of("driftScore", metrics.getDriftScore(), "threshold", 0.3));
            }
            
        } catch (Exception e) {
            logger.error("ML model performance monitoring failed", e);
            publishAlert("MONITORING_FAILURE", "LOW",
                "Performance monitoring failed: " + e.getMessage(),
                Map.of("component", "ML_MONITOR", "error", e.getMessage()));
        }
    }
    
    /**
     * Monitor system health every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void monitorSystemHealth() {
        try {
            logger.debug("Monitoring system health");
            
            SystemHealthMetrics health = collectSystemHealthMetrics();
            
            // Track system metrics
            meterRegistry.gauge("system.cpu.usage", health.getCpuUsage());
            meterRegistry.gauge("system.memory.usage", health.getMemoryUsage());
            meterRegistry.gauge("database.connection.pool.active", health.getActiveDbConnections());
            meterRegistry.gauge("analytics.cache.hit.ratio", health.getCacheHitRatio());
            meterRegistry.gauge("websocket.active.connections", health.getActiveWebSocketConnections());
            
            // Update cached values
            lastCpuUsage = health.getCpuUsage();
            lastMemoryUsage = health.getMemoryUsage();
            lastActiveConnections = health.getActiveWebSocketConnections();
            lastCacheHitRatio = health.getCacheHitRatio();
            
            // Generate health alerts
            if (health.getCpuUsage() > 0.8) {
                publishAlert("HIGH_CPU_USAGE", "MEDIUM",
                    "High CPU usage detected: " + String.format("%.1f%%", health.getCpuUsage() * 100),
                    Map.of("cpuUsage", health.getCpuUsage(), "threshold", 0.8));
            }
            
            if (health.getMemoryUsage() > 0.9) {
                publishAlert("HIGH_MEMORY_USAGE", "HIGH",
                    "High memory usage detected: " + String.format("%.1f%%", health.getMemoryUsage() * 100),
                    Map.of("memoryUsage", health.getMemoryUsage(), "threshold", 0.9));
            }
            
            if (health.getActiveDbConnections() > 80) {
                publishAlert("HIGH_DB_CONNECTIONS", "MEDIUM",
                    "High database connection usage: " + health.getActiveDbConnections(),
                    Map.of("connections", health.getActiveDbConnections(), "threshold", 80));
            }
            
            if (health.getCacheHitRatio() < 0.8) {
                publishAlert("LOW_CACHE_HIT_RATIO", "LOW",
                    "Cache hit ratio below threshold: " + String.format("%.1f%%", health.getCacheHitRatio() * 100),
                    Map.of("hitRatio", health.getCacheHitRatio(), "threshold", 0.8));
            }
            
        } catch (Exception e) {
            logger.error("System health monitoring failed", e);
            publishAlert("HEALTH_MONITORING_FAILURE", "MEDIUM",
                "System health monitoring failed: " + e.getMessage(),
                Map.of("component", "HEALTH_MONITOR", "error", e.getMessage()));
        }
    }
    
    /**
     * Generate performance reports every hour
     */
    @Scheduled(fixedRate = 3600000) // Every hour
    public void generatePerformanceReport() {
        try {
            logger.info("Generating performance report");
            
            Map<String, Object> report = new HashMap<>();
            
            // Request statistics
            int total = totalRequests.get();
            int successful = successfulRequests.get();
            int failed = failedRequests.get();
            
            report.put("requests", Map.of(
                "total", total,
                "successful", successful,
                "failed", failed,
                "successRate", total > 0 ? (double) successful / total : 0.0,
                "averageProcessingTime", total > 0 ? totalProcessingTime.get() / total : 0L
            ));
            
            // ML model performance
            report.put("mlModels", new HashMap<>(modelMetrics));
            
            // System health
            report.put("systemHealth", Map.of(
                "cpuUsage", lastCpuUsage,
                "memoryUsage", lastMemoryUsage,
                "activeConnections", lastActiveConnections,
                "cacheHitRatio", lastCacheHitRatio
            ));
            
            // Performance trends
            report.put("trends", analyzeTrends());
            
            report.put("generatedAt", LocalDateTime.now());
            
            // Log performance summary
            logger.info("Performance Report - Success Rate: {:.1f}%, Avg Processing Time: {}ms, ML Accuracy: {:.1f}%",
                total > 0 ? (successful * 100.0 / total) : 0.0,
                total > 0 ? totalProcessingTime.get() / total : 0L,
                modelMetrics.containsKey("primary") ? modelMetrics.get("primary").getAccuracy() * 100 : 0.0
            );
            
        } catch (Exception e) {
            logger.error("Performance report generation failed", e);
        }
    }
    
    /**
     * Health check implementation
     */
    @Override
    public Health health() {
        try {
            Health.Builder builder = Health.up();
            
            // Check basic metrics
            int total = totalRequests.get();
            int successful = successfulRequests.get();
            double successRate = total > 0 ? (double) successful / total : 1.0;
            
            builder.withDetail("analytics.requests.total", total);
            builder.withDetail("analytics.requests.successRate", successRate);
            
            // Check ML model health
            if (modelMetrics.containsKey("primary")) {
                MLModelMetrics metrics = modelMetrics.get("primary");
                builder.withDetail("ml.model.accuracy", metrics.getAccuracy());
                builder.withDetail("ml.model.latency", metrics.getAverageLatency());
                
                if (metrics.getAccuracy() < 0.7 || metrics.getAverageLatency() > 10000) {
                    builder.down();
                }
            }
            
            // Check system resources
            if (lastCpuUsage > 0.9 || lastMemoryUsage > 0.95) {
                builder.down();
            }
            
            builder.withDetail("system.cpu.usage", lastCpuUsage);
            builder.withDetail("system.memory.usage", lastMemoryUsage);
            builder.withDetail("lastChecked", LocalDateTime.now());
            
            return builder.build();
            
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .withDetail("timestamp", LocalDateTime.now())
                .build();
        }
    }
    
    /**
     * Get current performance metrics
     */
    public Map<String, Object> getCurrentMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        int total = totalRequests.get();
        int successful = successfulRequests.get();
        int failed = failedRequests.get();
        
        metrics.put("requests", Map.of(
            "total", total,
            "successful", successful,
            "failed", failed,
            "successRate", total > 0 ? (double) successful / total : 0.0
        ));
        
        metrics.put("processing", Map.of(
            "averageTime", total > 0 ? totalProcessingTime.get() / total : 0L,
            "totalTime", totalProcessingTime.get()
        ));
        
        if (modelMetrics.containsKey("primary")) {
            metrics.put("mlModel", modelMetrics.get("primary"));
        }
        
        metrics.put("system", Map.of(
            "cpuUsage", lastCpuUsage,
            "memoryUsage", lastMemoryUsage,
            "activeConnections", lastActiveConnections,
            "cacheHitRatio", lastCacheHitRatio
        ));
        
        metrics.put("timestamp", LocalDateTime.now());
        
        return metrics;
    }
    
    // Helper methods
    
    private void processAnalyticsRequest(Object request) {
        // Simulate request processing
        try {
            Thread.sleep(50 + (long)(Math.random() * 200)); // 50-250ms processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private MLModelMetrics collectMLModelMetrics() {
        // Simulate ML model metrics collection
        return new MLModelMetrics(
            0.85 + Math.random() * 0.1, // accuracy 85-95%
            0.82 + Math.random() * 0.1, // precision
            0.80 + Math.random() * 0.1, // recall
            1000 + (long)(Math.random() * 2000), // latency 1-3 seconds
            100 + (int)(Math.random() * 900), // total requests
            Math.random() * 0.5 // drift score 0-0.5
        );
    }
    
    private SystemHealthMetrics collectSystemHealthMetrics() {
        Runtime runtime = Runtime.getRuntime();
        
        double cpuUsage = 0.3 + Math.random() * 0.4; // 30-70% CPU
        double memoryUsage = (double) (runtime.totalMemory() - runtime.freeMemory()) / runtime.totalMemory();
        int activeDbConnections = 15 + (int)(Math.random() * 25); // 15-40 connections
        double cacheHitRatio = 0.85 + Math.random() * 0.1; // 85-95%
        int activeWebSocketConnections = 5 + (int)(Math.random() * 45); // 5-50 connections
        
        return new SystemHealthMetrics(cpuUsage, memoryUsage, activeDbConnections, 
            cacheHitRatio, activeWebSocketConnections);
    }
    
    private Map<String, Object> analyzeTrends() {
        Map<String, Object> trends = new HashMap<>();
        
        // Simplified trend analysis
        trends.put("requestTrend", "STABLE");
        trends.put("performanceTrend", "IMPROVING");
        trends.put("errorRateTrend", "DECREASING");
        trends.put("resourceUsageTrend", "STABLE");
        
        return trends;
    }
    
    private void publishAlert(String alertType, String severity, String message, Map<String, Object> data) {
        try {
            AnalyticsAlertEvent alert = new AnalyticsAlertEvent(
                alertType, severity, message, data, !"LOW".equals(severity)
            );
            eventPublisher.publishEvent(alert);
        } catch (Exception e) {
            logger.error("Failed to publish alert", e);
        }
    }
    
    // Metric classes
    
    public static class MLModelMetrics {
        private final double accuracy;
        private final double precision;
        private final double recall;
        private final long averageLatency;
        private final int totalRequests;
        private final double driftScore;
        
        public MLModelMetrics(double accuracy, double precision, double recall, 
                            long averageLatency, int totalRequests, double driftScore) {
            this.accuracy = accuracy;
            this.precision = precision;
            this.recall = recall;
            this.averageLatency = averageLatency;
            this.totalRequests = totalRequests;
            this.driftScore = driftScore;
        }
        
        // Getters
        public double getAccuracy() { return accuracy; }
        public double getPrecision() { return precision; }
        public double getRecall() { return recall; }
        public long getAverageLatency() { return averageLatency; }
        public int getTotalRequests() { return totalRequests; }
        public double getDriftScore() { return driftScore; }
    }
    
    public static class SystemHealthMetrics {
        private final double cpuUsage;
        private final double memoryUsage;
        private final int activeDbConnections;
        private final double cacheHitRatio;
        private final int activeWebSocketConnections;
        
        public SystemHealthMetrics(double cpuUsage, double memoryUsage, int activeDbConnections,
                                 double cacheHitRatio, int activeWebSocketConnections) {
            this.cpuUsage = cpuUsage;
            this.memoryUsage = memoryUsage;
            this.activeDbConnections = activeDbConnections;
            this.cacheHitRatio = cacheHitRatio;
            this.activeWebSocketConnections = activeWebSocketConnections;
        }
        
        // Getters
        public double getCpuUsage() { return cpuUsage; }
        public double getMemoryUsage() { return memoryUsage; }
        public int getActiveDbConnections() { return activeDbConnections; }
        public double getCacheHitRatio() { return cacheHitRatio; }
        public int getActiveWebSocketConnections() { return activeWebSocketConnections; }
    }
    
    public static class AnalyticsRequestEvent {
        private final Object request;
        private final LocalDateTime timestamp;
        
        public AnalyticsRequestEvent(Object request) {
            this.request = request;
            this.timestamp = LocalDateTime.now();
        }
        
        public Object getRequest() { return request; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
}
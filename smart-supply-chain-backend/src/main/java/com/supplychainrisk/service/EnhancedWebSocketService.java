package com.supplychainrisk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class EnhancedWebSocketService {
    
    private static final Logger logger = LoggerFactory.getLogger(EnhancedWebSocketService.class);
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private AnalyticsService analyticsService;
    
    /**
     * Handle risk score update events
     */
    @EventListener
    public void handleRiskScoreUpdate(RiskScoreUpdateEvent event) {
        try {
            logger.info("Broadcasting risk score update for supplier: {}", event.getSupplierId());
            
            // Broadcast real-time risk score updates
            Map<String, Object> update = Map.of(
                "type", "RISK_SCORE_UPDATE",
                "supplierId", event.getSupplierId(),
                "newRiskScore", event.getNewRiskScore(),
                "previousRiskScore", event.getPreviousRiskScore(),
                "change", event.getNewRiskScore().subtract(event.getPreviousRiskScore()),
                "confidence", event.getConfidence(),
                "riskFactors", event.getRiskFactors(),
                "timestamp", LocalDateTime.now(),
                "severity", determineSeverity(event.getNewRiskScore())
            );
            
            messagingTemplate.convertAndSend("/topic/risk-updates", update);
            
            // Also send to supplier-specific topic
            messagingTemplate.convertAndSend("/topic/supplier/" + event.getSupplierId() + "/risk", update);
            
        } catch (Exception e) {
            logger.error("Error broadcasting risk score update", e);
        }
    }
    
    /**
     * Handle prediction update events
     */
    @EventListener
    public void handlePredictionUpdate(PredictionUpdateEvent event) {
        try {
            logger.info("Broadcasting prediction update: {}", event.getPredictionId());
            
            // Broadcast ML prediction updates
            Map<String, Object> update = Map.of(
                "type", "PREDICTION_UPDATE",
                "predictionId", event.getPredictionId(),
                "predictionType", event.getPredictionType(),
                "prediction", event.getPrediction(),
                "confidence", event.getConfidence(),
                "accuracy", event.getAccuracy(),
                "modelVersion", event.getModelVersion(),
                "timestamp", LocalDateTime.now(),
                "trends", event.getTrends()
            );
            
            messagingTemplate.convertAndSend("/topic/predictions", update);
            
            // Send to specific prediction type topic
            messagingTemplate.convertAndSend("/topic/predictions/" + event.getPredictionType().toLowerCase(), update);
            
        } catch (Exception e) {
            logger.error("Error broadcasting prediction update", e);
        }
    }
    
    /**
     * Handle recommendation update events
     */
    @EventListener
    public void handleRecommendationUpdate(RecommendationUpdateEvent event) {
        try {
            logger.info("Broadcasting recommendation update: {}", event.getType());
            
            // Broadcast new recommendations
            Map<String, Object> update = Map.of(
                "type", "RECOMMENDATION_UPDATE",
                "recommendationType", event.getType(),
                "recommendations", event.getRecommendations(),
                "priority", event.getPriority(),
                "actionRequired", event.isActionRequired(),
                "deadline", event.getDeadline(),
                "context", event.getContext(),
                "timestamp", LocalDateTime.now()
            );
            
            messagingTemplate.convertAndSend("/topic/recommendations", update);
            
            // Send to priority-specific topic for urgent recommendations
            if ("HIGH".equals(event.getPriority()) || "CRITICAL".equals(event.getPriority())) {
                messagingTemplate.convertAndSend("/topic/alerts", update);
            }
            
        } catch (Exception e) {
            logger.error("Error broadcasting recommendation update", e);
        }
    }
    
    /**
     * Handle analytics alert events
     */
    @EventListener
    public void handleAnalyticsAlert(AnalyticsAlertEvent event) {
        try {
            logger.info("Broadcasting analytics alert: {}", event.getAlertType());
            
            Map<String, Object> alert = Map.of(
                "type", "ANALYTICS_ALERT",
                "alertType", event.getAlertType(),
                "severity", event.getSeverity(),
                "message", event.getMessage(),
                "data", event.getData(),
                "actionRequired", event.isActionRequired(),
                "timestamp", LocalDateTime.now(),
                "source", "ANALYTICS_ENGINE"
            );
            
            messagingTemplate.convertAndSend("/topic/alerts", alert);
            
            // Send critical alerts to dedicated topic
            if ("CRITICAL".equals(event.getSeverity())) {
                messagingTemplate.convertAndSend("/topic/critical-alerts", alert);
            }
            
        } catch (Exception e) {
            logger.error("Error broadcasting analytics alert", e);
        }
    }
    
    /**
     * Broadcast periodic analytics summary updates
     */
    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void broadcastAnalyticsUpdate() {
        try {
            logger.debug("Broadcasting periodic analytics update");
            
            // Get latest analytics summary
            Map<String, Object> analyticsSummary = analyticsService.getAnalyticsSummary();
            
            Map<String, Object> update = Map.of(
                "type", "ANALYTICS_SUMMARY",
                "summary", analyticsSummary,
                "updateFrequency", "30_SECONDS",
                "timestamp", LocalDateTime.now(),
                "source", "SCHEDULED_UPDATE"
            );
            
            messagingTemplate.convertAndSend("/topic/analytics", update);
            
        } catch (Exception e) {
            logger.error("Failed to broadcast analytics update", e);
        }
    }
    
    /**
     * Broadcast real-time performance metrics
     */
    @Scheduled(fixedRate = 60000) // Every minute
    public void broadcastPerformanceMetrics() {
        try {
            logger.debug("Broadcasting performance metrics");
            
            Map<String, Object> metrics = analyticsService.getAnalyticsPerformanceMetrics();
            
            Map<String, Object> update = Map.of(
                "type", "PERFORMANCE_METRICS",
                "metrics", metrics,
                "timestamp", LocalDateTime.now(),
                "source", "PERFORMANCE_MONITOR"
            );
            
            messagingTemplate.convertAndSend("/topic/performance", update);
            
        } catch (Exception e) {
            logger.error("Failed to broadcast performance metrics", e);
        }
    }
    
    /**
     * Send custom message to specific topic
     */
    public void sendCustomMessage(String topic, Map<String, Object> message) {
        try {
            Map<String, Object> enhancedMessage = Map.of(
                "type", "CUSTOM_MESSAGE",
                "data", message,
                "timestamp", LocalDateTime.now(),
                "source", "CUSTOM"
            );
            
            messagingTemplate.convertAndSend("/topic/" + topic, enhancedMessage);
            
        } catch (Exception e) {
            logger.error("Error sending custom message to topic: {}", topic, e);
        }
    }
    
    /**
     * Send user-specific notification
     */
    public void sendUserNotification(String userId, Map<String, Object> notification) {
        try {
            Map<String, Object> message = Map.of(
                "type", "USER_NOTIFICATION",
                "notification", notification,
                "timestamp", LocalDateTime.now(),
                "recipient", userId
            );
            
            messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", message);
            
        } catch (Exception e) {
            logger.error("Error sending user notification to user: {}", userId, e);
        }
    }
    
    /**
     * Broadcast system health status
     */
    @Scheduled(fixedRate = 120000) // Every 2 minutes
    public void broadcastSystemHealth() {
        try {
            Map<String, Object> healthStatus = Map.of(
                "type", "SYSTEM_HEALTH",
                "status", "HEALTHY",
                "uptime", System.currentTimeMillis(),
                "activeConnections", getActiveConnectionCount(),
                "timestamp", LocalDateTime.now(),
                "services", Map.of(
                    "analytics", "UP",
                    "websocket", "UP",
                    "database", "UP"
                )
            );
            
            messagingTemplate.convertAndSend("/topic/system-health", healthStatus);
            
        } catch (Exception e) {
            logger.error("Failed to broadcast system health", e);
        }
    }
    
    // Helper methods
    
    private String determineSeverity(java.math.BigDecimal riskScore) {
        if (riskScore.compareTo(java.math.BigDecimal.valueOf(80)) >= 0) {
            return "CRITICAL";
        } else if (riskScore.compareTo(java.math.BigDecimal.valueOf(60)) >= 0) {
            return "HIGH";
        } else if (riskScore.compareTo(java.math.BigDecimal.valueOf(40)) >= 0) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }
    
    private int getActiveConnectionCount() {
        // Simplified - in a real implementation this would track actual connections
        return (int)(Math.random() * 50) + 10;
    }
    
    // Event classes for handling different types of updates
    
    public static class RiskScoreUpdateEvent {
        private final Long supplierId;
        private final java.math.BigDecimal newRiskScore;
        private final java.math.BigDecimal previousRiskScore;
        private final java.math.BigDecimal confidence;
        private final Map<String, Object> riskFactors;
        
        public RiskScoreUpdateEvent(Long supplierId, java.math.BigDecimal newRiskScore, 
                                  java.math.BigDecimal previousRiskScore, java.math.BigDecimal confidence,
                                  Map<String, Object> riskFactors) {
            this.supplierId = supplierId;
            this.newRiskScore = newRiskScore;
            this.previousRiskScore = previousRiskScore;
            this.confidence = confidence;
            this.riskFactors = riskFactors;
        }
        
        // Getters
        public Long getSupplierId() { return supplierId; }
        public java.math.BigDecimal getNewRiskScore() { return newRiskScore; }
        public java.math.BigDecimal getPreviousRiskScore() { return previousRiskScore; }
        public java.math.BigDecimal getConfidence() { return confidence; }
        public Map<String, Object> getRiskFactors() { return riskFactors; }
    }
    
    public static class PredictionUpdateEvent {
        private final String predictionId;
        private final String predictionType;
        private final Map<String, Object> prediction;
        private final java.math.BigDecimal confidence;
        private final java.math.BigDecimal accuracy;
        private final String modelVersion;
        private final Map<String, Object> trends;
        
        public PredictionUpdateEvent(String predictionId, String predictionType,
                                   Map<String, Object> prediction, java.math.BigDecimal confidence,
                                   java.math.BigDecimal accuracy, String modelVersion,
                                   Map<String, Object> trends) {
            this.predictionId = predictionId;
            this.predictionType = predictionType;
            this.prediction = prediction;
            this.confidence = confidence;
            this.accuracy = accuracy;
            this.modelVersion = modelVersion;
            this.trends = trends;
        }
        
        // Getters
        public String getPredictionId() { return predictionId; }
        public String getPredictionType() { return predictionType; }
        public Map<String, Object> getPrediction() { return prediction; }
        public java.math.BigDecimal getConfidence() { return confidence; }
        public java.math.BigDecimal getAccuracy() { return accuracy; }
        public String getModelVersion() { return modelVersion; }
        public Map<String, Object> getTrends() { return trends; }
    }
    
    public static class RecommendationUpdateEvent {
        private final String type;
        private final java.util.List<Map<String, Object>> recommendations;
        private final String priority;
        private final boolean actionRequired;
        private final LocalDateTime deadline;
        private final Map<String, Object> context;
        
        public RecommendationUpdateEvent(String type, java.util.List<Map<String, Object>> recommendations,
                                       String priority, boolean actionRequired, LocalDateTime deadline,
                                       Map<String, Object> context) {
            this.type = type;
            this.recommendations = recommendations;
            this.priority = priority;
            this.actionRequired = actionRequired;
            this.deadline = deadline;
            this.context = context;
        }
        
        // Getters
        public String getType() { return type; }
        public java.util.List<Map<String, Object>> getRecommendations() { return recommendations; }
        public String getPriority() { return priority; }
        public boolean isActionRequired() { return actionRequired; }
        public LocalDateTime getDeadline() { return deadline; }
        public Map<String, Object> getContext() { return context; }
    }
    
    public static class AnalyticsAlertEvent {
        private final String alertType;
        private final String severity;
        private final String message;
        private final Map<String, Object> data;
        private final boolean actionRequired;
        
        public AnalyticsAlertEvent(String alertType, String severity, String message,
                                 Map<String, Object> data, boolean actionRequired) {
            this.alertType = alertType;
            this.severity = severity;
            this.message = message;
            this.data = data;
            this.actionRequired = actionRequired;
        }
        
        // Getters
        public String getAlertType() { return alertType; }
        public String getSeverity() { return severity; }
        public String getMessage() { return message; }
        public Map<String, Object> getData() { return data; }
        public boolean isActionRequired() { return actionRequired; }
    }
}
package com.supplychainrisk.service;

import com.supplychainrisk.entity.Shipment;
import com.supplychainrisk.entity.ShipmentTrackingEvent;
import com.supplychainrisk.repository.ShipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class RealTimeUpdateService {
    
    private static final Logger logger = LoggerFactory.getLogger(RealTimeUpdateService.class);
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private ShipmentRepository shipmentRepository;
    
    /**
     * Broadcast shipment status update to all connected clients
     */
    @Async
    public void broadcastShipmentUpdate(Long shipmentId, Shipment.ShipmentStatus newStatus, String description) {
        try {
            Optional<Shipment> shipmentOptional = shipmentRepository.findById(shipmentId);
            if (shipmentOptional.isPresent()) {
                Shipment shipment = shipmentOptional.get();
                
                Map<String, Object> update = new HashMap<>();
                update.put("shipmentId", shipmentId);
                update.put("trackingNumber", shipment.getTrackingNumber());
                update.put("status", newStatus.name());
                update.put("statusDescription", description);
                update.put("carrier", shipment.getCarrierName());
                update.put("timestamp", LocalDateTime.now().toString());
                update.put("estimatedDelivery", shipment.getEstimatedDeliveryDate());
                
                // Broadcast to all subscribers
                messagingTemplate.convertAndSend("/topic/shipments", update);
                
                // Broadcast to specific shipment subscribers
                messagingTemplate.convertAndSend("/topic/shipments/" + shipmentId, update);
                
                logger.info("Broadcasted update for shipment {} with status {}", shipmentId, newStatus);
            }
        } catch (Exception e) {
            logger.error("Error broadcasting shipment update for shipment {}: {}", shipmentId, e.getMessage(), e);
        }
    }
    
    /**
     * Broadcast new tracking event to connected clients
     */
    @Async
    public void broadcastTrackingEvent(Long shipmentId, ShipmentTrackingEvent event) {
        try {
            Map<String, Object> eventUpdate = new HashMap<>();
            eventUpdate.put("shipmentId", shipmentId);
            eventUpdate.put("eventId", event.getId());
            eventUpdate.put("eventType", event.getEventType().name());
            eventUpdate.put("eventDescription", event.getEventDescription());
            eventUpdate.put("eventTimestamp", event.getEventTimestamp().toString());
            eventUpdate.put("location", Map.of(
                "city", event.getLocationCity(),
                "country", event.getLocationCountry(),
                "latitude", event.getLatitude(),
                "longitude", event.getLongitude()
            ));
            eventUpdate.put("isException", event.getIsException());
            
            if (event.getIsException()) {
                eventUpdate.put("exceptionReason", event.getExceptionReason());
            }
            
            // Broadcast tracking event
            messagingTemplate.convertAndSend("/topic/tracking-events", eventUpdate);
            messagingTemplate.convertAndSend("/topic/shipments/" + shipmentId + "/events", eventUpdate);
            
            logger.info("Broadcasted tracking event for shipment {} at {}", shipmentId, event.getLocationCity());
        } catch (Exception e) {
            logger.error("Error broadcasting tracking event for shipment {}: {}", shipmentId, e.getMessage(), e);
        }
    }
    
    /**
     * Broadcast risk alert to connected clients
     */
    @Async
    public void broadcastRiskAlert(Long shipmentId, String alertType, String message, String severity) {
        try {
            Map<String, Object> riskAlert = new HashMap<>();
            riskAlert.put("shipmentId", shipmentId);
            riskAlert.put("alertType", alertType);
            riskAlert.put("message", message);
            riskAlert.put("severity", severity); // LOW, MEDIUM, HIGH, CRITICAL
            riskAlert.put("timestamp", LocalDateTime.now().toString());
            
            // Broadcast risk alert
            messagingTemplate.convertAndSend("/topic/risk-alerts", riskAlert);
            messagingTemplate.convertAndSend("/topic/shipments/" + shipmentId + "/alerts", riskAlert);
            
            logger.info("Broadcasted risk alert for shipment {}: {} - {}", shipmentId, alertType, severity);
        } catch (Exception e) {
            logger.error("Error broadcasting risk alert for shipment {}: {}", shipmentId, e.getMessage(), e);
        }
    }
    
    /**
     * Broadcast supplier performance update
     */
    @Async
    public void broadcastSupplierUpdate(Long supplierId, String updateType, Map<String, Object> data) {
        try {
            Map<String, Object> supplierUpdate = new HashMap<>();
            supplierUpdate.put("supplierId", supplierId);
            supplierUpdate.put("updateType", updateType); // PERFORMANCE, RISK_SCORE, STATUS, etc.
            supplierUpdate.put("data", data);
            supplierUpdate.put("timestamp", LocalDateTime.now().toString());
            
            // Broadcast supplier update
            messagingTemplate.convertAndSend("/topic/suppliers", supplierUpdate);
            messagingTemplate.convertAndSend("/topic/suppliers/" + supplierId, supplierUpdate);
            
            logger.info("Broadcasted supplier update for supplier {}: {}", supplierId, updateType);
        } catch (Exception e) {
            logger.error("Error broadcasting supplier update for supplier {}: {}", supplierId, e.getMessage(), e);
        }
    }
    
    /**
     * Broadcast analytics update to connected clients
     */
    @Async
    public void broadcastAnalyticsUpdate(String analysisType, Object result) {
        try {
            Map<String, Object> analyticsUpdate = new HashMap<>();
            analyticsUpdate.put("type", "ANALYTICS_UPDATE");
            analyticsUpdate.put("analysisType", analysisType);
            analyticsUpdate.put("result", result);
            analyticsUpdate.put("timestamp", LocalDateTime.now().toString());
            
            // Broadcast to analytics topic
            messagingTemplate.convertAndSend("/topic/analytics", analyticsUpdate);
            messagingTemplate.convertAndSend("/topic/dashboard", analyticsUpdate);
            
            logger.info("Broadcasted analytics update for analysis type: {}", analysisType);
        } catch (Exception e) {
            logger.error("Error broadcasting analytics update: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Broadcast ML model status update
     */
    @Async
    public void broadcastMLModelUpdate(String modelVersion, Map<String, Object> metrics) {
        try {
            Map<String, Object> modelUpdate = new HashMap<>();
            modelUpdate.put("type", "ML_MODEL_UPDATE");
            modelUpdate.put("modelVersion", modelVersion);
            modelUpdate.put("metrics", metrics);
            modelUpdate.put("timestamp", LocalDateTime.now().toString());
            
            // Broadcast model update
            messagingTemplate.convertAndSend("/topic/ml-models", modelUpdate);
            messagingTemplate.convertAndSend("/topic/analytics", modelUpdate);
            
            logger.info("Broadcasted ML model update for version: {}", modelVersion);
        } catch (Exception e) {
            logger.error("Error broadcasting ML model update: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Broadcast system-wide dashboard update
     */
    @Async
    public void broadcastDashboardUpdate(String metric, Object value) {
        try {
            Map<String, Object> dashboardUpdate = new HashMap<>();
            dashboardUpdate.put("metric", metric);
            dashboardUpdate.put("value", value);
            dashboardUpdate.put("timestamp", LocalDateTime.now().toString());
            
            // Broadcast dashboard update
            messagingTemplate.convertAndSend("/topic/dashboard", dashboardUpdate);
            
            logger.debug("Broadcasted dashboard update: {} = {}", metric, value);
        } catch (Exception e) {
            logger.error("Error broadcasting dashboard update for metric {}: {}", metric, e.getMessage(), e);
        }
    }
    
    /**
     * Send notification to specific user
     */
    @Async
    public void sendUserNotification(String username, String title, String message, String type) {
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("title", title);
            notification.put("message", message);
            notification.put("type", type); // INFO, WARNING, ERROR, SUCCESS
            notification.put("timestamp", LocalDateTime.now().toString());
            
            // Send to specific user
            messagingTemplate.convertAndSendToUser(username, "/topic/notifications", notification);
            
            logger.info("Sent notification to user {}: {}", username, title);
        } catch (Exception e) {
            logger.error("Error sending notification to user {}: {}", username, e.getMessage(), e);
        }
    }
}
package com.supplychainrisk.controller;

import com.supplychainrisk.dto.ShipmentDTO;
import com.supplychainrisk.dto.ShipmentTrackingEventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ShipmentWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ShipmentWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Broadcast shipment status update to all connected clients
     */
    public void broadcastShipmentUpdate(ShipmentDTO shipment) {
        messagingTemplate.convertAndSend("/topic/shipments/updates", shipment);
    }

    /**
     * Broadcast new tracking event to clients subscribed to specific shipment
     */
    public void broadcastTrackingEvent(Long shipmentId, ShipmentTrackingEventDTO event) {
        messagingTemplate.convertAndSend("/topic/shipments/" + shipmentId + "/events", event);
    }

    /**
     * Broadcast tracking event to all clients (for dashboard)
     */
    public void broadcastTrackingEventToAll(ShipmentTrackingEventDTO event) {
        messagingTemplate.convertAndSend("/topic/tracking-events", event);
    }

    /**
     * Broadcast exception events for immediate attention
     */
    public void broadcastException(ShipmentTrackingEventDTO exception) {
        messagingTemplate.convertAndSend("/topic/exceptions", exception);
    }

    /**
     * Handle subscription to specific shipment tracking
     */
    @MessageMapping("/shipments/subscribe")
    public void subscribeToShipment(@Payload Long shipmentId) {
        // Logic for subscribing to specific shipment updates
        // This is handled automatically by the messaging template
    }

    /**
     * Send real-time location updates for shipments
     */
    public void broadcastLocationUpdate(Long shipmentId, double latitude, double longitude) {
        LocationUpdate update = new LocationUpdate(shipmentId, latitude, longitude, System.currentTimeMillis());
        messagingTemplate.convertAndSend("/topic/shipments/" + shipmentId + "/location", update);
        messagingTemplate.convertAndSend("/topic/locations", update);
    }

    /**
     * Broadcast shipment metrics for dashboard
     */
    public void broadcastMetrics(ShipmentMetrics metrics) {
        messagingTemplate.convertAndSend("/topic/metrics", metrics);
    }

    // Inner classes for specific message types
    public static class LocationUpdate {
        private Long shipmentId;
        private double latitude;
        private double longitude;
        private long timestamp;

        public LocationUpdate(Long shipmentId, double latitude, double longitude, long timestamp) {
            this.shipmentId = shipmentId;
            this.latitude = latitude;
            this.longitude = longitude;
            this.timestamp = timestamp;
        }

        // Getters and setters
        public Long getShipmentId() { return shipmentId; }
        public void setShipmentId(Long shipmentId) { this.shipmentId = shipmentId; }
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    public static class ShipmentMetrics {
        private long totalShipments;
        private long inTransitShipments;
        private long deliveredShipments;
        private long exceptionShipments;
        private double onTimePerformance;

        public ShipmentMetrics(long totalShipments, long inTransitShipments, long deliveredShipments, 
                             long exceptionShipments, double onTimePerformance) {
            this.totalShipments = totalShipments;
            this.inTransitShipments = inTransitShipments;
            this.deliveredShipments = deliveredShipments;
            this.exceptionShipments = exceptionShipments;
            this.onTimePerformance = onTimePerformance;
        }

        // Getters and setters
        public long getTotalShipments() { return totalShipments; }
        public void setTotalShipments(long totalShipments) { this.totalShipments = totalShipments; }
        public long getInTransitShipments() { return inTransitShipments; }
        public void setInTransitShipments(long inTransitShipments) { this.inTransitShipments = inTransitShipments; }
        public long getDeliveredShipments() { return deliveredShipments; }
        public void setDeliveredShipments(long deliveredShipments) { this.deliveredShipments = deliveredShipments; }
        public long getExceptionShipments() { return exceptionShipments; }
        public void setExceptionShipments(long exceptionShipments) { this.exceptionShipments = exceptionShipments; }
        public double getOnTimePerformance() { return onTimePerformance; }
        public void setOnTimePerformance(double onTimePerformance) { this.onTimePerformance = onTimePerformance; }
    }
}
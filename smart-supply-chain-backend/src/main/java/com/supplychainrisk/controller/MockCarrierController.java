package com.supplychainrisk.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/mock-apis/carriers")
@CrossOrigin(origins = "http://localhost:3000")
public class MockCarrierController {
    
    private static final Logger logger = LoggerFactory.getLogger(MockCarrierController.class);
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
    @GetMapping("/dhl/track/{trackingNumber}")
    public ResponseEntity<?> getDHLTracking(@PathVariable String trackingNumber) {
        logger.info("DHL tracking request for: {}", trackingNumber);
        
        Map<String, Object> response = new HashMap<>();
        response.put("trackingNumber", trackingNumber);
        response.put("carrier", "DHL Express");
        response.put("serviceType", "EXPRESS WORLDWIDE");
        response.put("status", "IN_TRANSIT");
        response.put("statusDescription", "The shipment is on its way");
        
        // Mock tracking events
        List<Map<String, Object>> events = new ArrayList<>();
        
        events.add(createDHLEvent("2024-01-15T10:30:00Z", "Shipment picked up", 
            "FRANKFURT", "DE", "PICKUP"));
        events.add(createDHLEvent("2024-01-15T14:15:00Z", "Processed at DHL facility", 
            "FRANKFURT", "DE", "TRANSIT"));
        events.add(createDHLEvent("2024-01-16T08:45:00Z", "Departed facility", 
            "FRANKFURT", "DE", "TRANSIT"));
        events.add(createDHLEvent("2024-01-16T22:30:00Z", "Arrived at destination country", 
            "NEW YORK", "US", "TRANSIT"));
        events.add(createDHLEvent("2024-01-17T06:00:00Z", "Customs clearance completed", 
            "NEW YORK", "US", "TRANSIT"));
        events.add(createDHLEvent("2024-01-17T09:15:00Z", "Out for delivery", 
            "NEW YORK", "US", "OUT_FOR_DELIVERY"));
        
        response.put("events", events);
        response.put("estimatedDelivery", "2024-01-17T17:00:00Z");
        response.put("signature", null);
        response.put("weight", "2.5 kg");
        response.put("dimensions", "30x20x15 cm");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/fedex/track/{trackingNumber}")
    public ResponseEntity<?> getFedExTracking(@PathVariable String trackingNumber) {
        logger.info("FedEx tracking request for: {}", trackingNumber);
        
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> trackResults = new HashMap<>();
        
        trackResults.put("trackingNumber", trackingNumber);
        trackResults.put("status", "IN_TRANSIT");
        trackResults.put("statusDescription", "In transit");
        trackResults.put("serviceType", "FEDEX_EXPRESS_SAVER");
        
        // Mock scan events
        List<Map<String, Object>> scanEvents = new ArrayList<>();
        
        scanEvents.add(createFedExEvent("2024-01-15T11:00:00.000Z", "Picked up", 
            "MEMPHIS", "TN", "US", "PU"));
        scanEvents.add(createFedExEvent("2024-01-15T16:30:00.000Z", "Arrived at FedEx location", 
            "MEMPHIS", "TN", "US", "AR"));
        scanEvents.add(createFedExEvent("2024-01-15T20:45:00.000Z", "Departed FedEx location", 
            "MEMPHIS", "TN", "US", "DP"));
        scanEvents.add(createFedExEvent("2024-01-16T14:20:00.000Z", "At destination sort facility", 
            "LOS ANGELES", "CA", "US", "AR"));
        scanEvents.add(createFedExEvent("2024-01-17T08:30:00.000Z", "On FedEx vehicle for delivery", 
            "LOS ANGELES", "CA", "US", "OD"));
        
        trackResults.put("scanEvents", scanEvents);
        trackResults.put("estimatedDeliveryTimeWindow", Map.of(
            "description", "End of day",
            "window", Map.of(
                "begins", "2024-01-17T10:30:00.000Z",
                "ends", "2024-01-17T20:00:00.000Z"
            )
        ));
        
        response.put("trackResults", Arrays.asList(trackResults));
        response.put("successful", true);
        response.put("complete", true);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/ups/track/{trackingNumber}")
    public ResponseEntity<?> getUPSTracking(@PathVariable String trackingNumber) {
        logger.info("UPS tracking request for: {}", trackingNumber);
        
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> trackResponse = new HashMap<>();
        Map<String, Object> shipment = new HashMap<>();
        
        shipment.put("trackingNumber", trackingNumber);
        shipment.put("service", Map.of(
            "code", "03",
            "description", "UPS Ground"
        ));
        
        Map<String, Object> currentStatus = new HashMap<>();
        currentStatus.put("type", "M");
        currentStatus.put("description", "Manifest Pickup");
        currentStatus.put("code", "MP");
        currentStatus.put("date", "20240117");
        currentStatus.put("time", "140000");
        
        shipment.put("currentStatus", currentStatus);
        
        // Mock package progress
        List<Map<String, Object>> activities = new ArrayList<>();
        
        activities.add(createUPSActivity("20240115", "093000", "ATLANTA", "GA", "US", 
            "Origin Scan", "OR"));
        activities.add(createUPSActivity("20240115", "183000", "ATLANTA", "GA", "US", 
            "Departure Scan", "DP"));
        activities.add(createUPSActivity("20240116", "065000", "LOUISVILLE", "KY", "US", 
            "Arrival Scan", "AR"));
        activities.add(createUPSActivity("20240116", "120000", "LOUISVILLE", "KY", "US", 
            "Departure Scan", "DP"));
        activities.add(createUPSActivity("20240117", "084500", "CHICAGO", "IL", "US", 
            "Arrival Scan", "AR"));
        activities.add(createUPSActivity("20240117", "140000", "CHICAGO", "IL", "US", 
            "Out For Delivery", "OFD"));
        
        shipment.put("package", Map.of("activities", activities));
        shipment.put("deliveryDate", Arrays.asList(Map.of(
            "type", "DEL",
            "date", "20240117"
        )));
        
        trackResponse.put("shipment", Arrays.asList(shipment));
        response.put("trackResponse", trackResponse);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/maersk/track/{containerNumber}")
    public ResponseEntity<?> getMaerskTracking(@PathVariable String containerNumber) {
        logger.info("Maersk tracking request for: {}", containerNumber);
        
        Map<String, Object> response = new HashMap<>();
        response.put("containerNumber", containerNumber);
        response.put("bookingNumber", "MAEU" + containerNumber.substring(4));
        response.put("status", "IN_TRANSIT");
        response.put("transportMode", "VESSEL");
        
        // Mock voyage information
        Map<String, Object> voyage = new HashMap<>();
        voyage.put("vesselName", "MAERSK SHANGHAI");
        voyage.put("voyageNumber", "218W");
        voyage.put("service", "AE7");
        
        response.put("voyage", voyage);
        
        // Mock container events
        List<Map<String, Object>> events = new ArrayList<>();
        
        events.add(createMaerskEvent("2024-01-10T14:30:00Z", "Container gated in", 
            "SHANGHAI", "CN", "GATE_IN"));
        events.add(createMaerskEvent("2024-01-12T08:15:00Z", "Loaded on vessel", 
            "SHANGHAI", "CN", "LOAD"));
        events.add(createMaerskEvent("2024-01-12T18:00:00Z", "Vessel departure", 
            "SHANGHAI", "CN", "DEPARTURE"));
        events.add(createMaerskEvent("2024-01-28T06:30:00Z", "Vessel arrival", 
            "LOS ANGELES", "US", "ARRIVAL"));
        events.add(createMaerskEvent("2024-01-29T10:45:00Z", "Discharged from vessel", 
            "LOS ANGELES", "US", "DISCHARGE"));
        events.add(createMaerskEvent("2024-01-29T15:20:00Z", "Available for pickup", 
            "LOS ANGELES", "US", "AVAILABLE"));
        
        response.put("events", events);
        response.put("estimatedTimeOfArrival", "2024-01-28T06:30:00Z");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/msc/track/{containerNumber}")
    public ResponseEntity<?> getMSCTracking(@PathVariable String containerNumber) {
        logger.info("MSC tracking request for: {}", containerNumber);
        
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> tracking = new HashMap<>();
        
        tracking.put("containerNumber", containerNumber);
        tracking.put("status", "IN TRANSIT");
        tracking.put("service", "CONDOR");
        tracking.put("voyage", "2401W");
        tracking.put("vessel", "MSC ALTAIR");
        
        // Mock milestones
        List<Map<String, Object>> milestones = new ArrayList<>();
        
        milestones.add(createMSCMilestone("2024-01-08T12:00:00Z", "Container received", 
            "NINGBO", "CN", "RECEIVED"));
        milestones.add(createMSCMilestone("2024-01-10T16:30:00Z", "Loaded onto vessel", 
            "NINGBO", "CN", "LOADED"));
        milestones.add(createMSCMilestone("2024-01-11T02:00:00Z", "Vessel sailed", 
            "NINGBO", "CN", "SAILED"));
        milestones.add(createMSCMilestone("2024-01-25T14:15:00Z", "Vessel arrived", 
            "ROTTERDAM", "NL", "ARRIVED"));
        milestones.add(createMSCMilestone("2024-01-26T09:30:00Z", "Container discharged", 
            "ROTTERDAM", "NL", "DISCHARGED"));
        milestones.add(createMSCMilestone("2024-01-26T11:45:00Z", "Ready for delivery", 
            "ROTTERDAM", "NL", "READY"));
        
        tracking.put("milestones", milestones);
        tracking.put("eta", "2024-01-25T14:15:00Z");
        
        response.put("tracking", tracking);
        response.put("success", true);
        
        return ResponseEntity.ok(response);
    }
    
    // Helper methods for creating event objects
    
    private Map<String, Object> createDHLEvent(String timestamp, String description, 
                                              String location, String country, String status) {
        Map<String, Object> event = new HashMap<>();
        event.put("timestamp", timestamp);
        event.put("description", description);
        event.put("location", Map.of(
            "name", location,
            "country", country
        ));
        event.put("status", status);
        return event;
    }
    
    private Map<String, Object> createFedExEvent(String timestamp, String description,
                                                String city, String state, String country, String eventType) {
        Map<String, Object> event = new HashMap<>();
        event.put("date", timestamp);
        event.put("eventDescription", description);
        event.put("eventType", eventType);
        event.put("location", Map.of(
            "city", city,
            "stateOrProvinceCode", state,
            "countryCode", country
        ));
        return event;
    }
    
    private Map<String, Object> createUPSActivity(String date, String time, String city, 
                                                 String state, String country, String description, String code) {
        Map<String, Object> activity = new HashMap<>();
        activity.put("date", date);
        activity.put("time", time);
        activity.put("location", Map.of(
            "address", Map.of(
                "city", city,
                "stateProvinceCode", state,
                "countryCode", country
            )
        ));
        activity.put("status", Map.of(
            "description", description,
            "code", code
        ));
        return activity;
    }
    
    private Map<String, Object> createMaerskEvent(String timestamp, String description,
                                                 String location, String country, String eventType) {
        Map<String, Object> event = new HashMap<>();
        event.put("actualTime", timestamp);
        event.put("description", description);
        event.put("location", Map.of(
            "name", location,
            "country", country
        ));
        event.put("eventType", eventType);
        return event;
    }
    
    private Map<String, Object> createMSCMilestone(String timestamp, String description,
                                                  String location, String country, String type) {
        Map<String, Object> milestone = new HashMap<>();
        milestone.put("actualTime", timestamp);
        milestone.put("description", description);
        milestone.put("location", Map.of(
            "name", location,
            "country", country
        ));
        milestone.put("type", type);
        return milestone;
    }
}
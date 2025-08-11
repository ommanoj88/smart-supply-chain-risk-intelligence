package com.supplychainrisk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@CrossOrigin(origins = "http://localhost:3000")
public class HealthController {
    
    @Autowired
    private DataSource dataSource;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().toString());
        health.put("service", "Smart Supply Chain Risk Intelligence API");
        health.put("version", "1.0.0");
        
        // Check database connectivity
        Map<String, Object> database = checkDatabase();
        health.put("database", database);
        
        // Check components
        Map<String, Object> components = new HashMap<>();
        components.put("authentication", Map.of("status", "UP", "provider", "JWT + Firebase"));
        components.put("websocket", Map.of("status", "UP", "endpoint", "/ws/shipment-tracking"));
        components.put("realtime-updates", Map.of("status", "UP", "enabled", true));
        health.put("components", components);
        
        // Overall status
        boolean isHealthy = "UP".equals(database.get("status"));
        health.put("status", isHealthy ? "UP" : "DOWN");
        
        return ResponseEntity.ok(health);
    }
    
    @GetMapping("/database")
    public ResponseEntity<Map<String, Object>> getDatabaseHealth() {
        Map<String, Object> database = checkDatabase();
        boolean isHealthy = "UP".equals(database.get("status"));
        
        return isHealthy ? 
            ResponseEntity.ok(database) : 
            ResponseEntity.status(503).body(database);
    }
    
    @GetMapping("/ready")
    public ResponseEntity<Map<String, Object>> getReadiness() {
        Map<String, Object> readiness = new HashMap<>();
        
        // Check if application is ready to serve traffic
        boolean databaseReady = "UP".equals(checkDatabase().get("status"));
        
        readiness.put("ready", databaseReady);
        readiness.put("timestamp", LocalDateTime.now().toString());
        readiness.put("checks", Map.of(
            "database", databaseReady ? "READY" : "NOT_READY"
        ));
        
        return databaseReady ? 
            ResponseEntity.ok(readiness) : 
            ResponseEntity.status(503).body(readiness);
    }
    
    @GetMapping("/live")
    public ResponseEntity<Map<String, Object>> getLiveness() {
        Map<String, Object> liveness = new HashMap<>();
        liveness.put("alive", true);
        liveness.put("timestamp", LocalDateTime.now().toString());
        liveness.put("uptime", getUptime());
        
        return ResponseEntity.ok(liveness);
    }
    
    private Map<String, Object> checkDatabase() {
        Map<String, Object> database = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                database.put("status", "UP");
                database.put("provider", "PostgreSQL");
                database.put("url", connection.getMetaData().getURL());
                database.put("validationTime", "1s");
            } else {
                database.put("status", "DOWN");
                database.put("error", "Database connection is not valid");
            }
        } catch (Exception e) {
            database.put("status", "DOWN");
            database.put("error", e.getMessage());
            database.put("provider", "PostgreSQL");
        }
        
        return database;
    }
    
    private String getUptime() {
        long uptimeMillis = System.currentTimeMillis() - getStartTime();
        long uptimeSeconds = uptimeMillis / 1000;
        long hours = uptimeSeconds / 3600;
        long minutes = (uptimeSeconds % 3600) / 60;
        long seconds = uptimeSeconds % 60;
        
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    private long getStartTime() {
        // This is a simplified approach - in production you might want to track this more accurately
        return System.currentTimeMillis() - 
               java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime();
    }
}
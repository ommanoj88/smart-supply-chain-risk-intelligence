package com.supplychainrisk.controller;

import com.supplychainrisk.service.DataSeedingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    @Autowired
    private DataSeedingService dataSeedingService;
    
    @PostMapping("/seed-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> seedData() {
        try {
            logger.info("Starting data seeding process...");
            dataSeedingService.seedAllData();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Data seeding completed successfully"
            ));
            
        } catch (Exception e) {
            logger.error("Data seeding failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Data seeding failed: " + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/data-stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUDITOR')")
    public ResponseEntity<?> getDataStats() {
        try {
            // This would be implemented to return counts of various entities
            return ResponseEntity.ok(Map.of(
                "message", "Data statistics endpoint - to be implemented"
            ));
            
        } catch (Exception e) {
            logger.error("Error retrieving data stats: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to retrieve data statistics"
            ));
        }
    }
}
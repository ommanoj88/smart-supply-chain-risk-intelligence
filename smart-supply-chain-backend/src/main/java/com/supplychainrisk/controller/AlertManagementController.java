package com.supplychainrisk.controller;

import com.supplychainrisk.entity.Alert;
import com.supplychainrisk.entity.AlertConfiguration;
import com.supplychainrisk.entity.User;
import com.supplychainrisk.service.AdvancedNotificationService;
import com.supplychainrisk.repository.AlertRepository;
import com.supplychainrisk.repository.AlertConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/alert-management")
@CrossOrigin(origins = "http://localhost:3000")
public class AlertManagementController {
    
    @Autowired
    private AdvancedNotificationService advancedNotificationService;
    
    @Autowired
    private AlertRepository alertRepository;
    
    @Autowired
    private AlertConfigurationRepository alertConfigurationRepository;
    
    @GetMapping("/alerts")
    public ResponseEntity<Page<Alert>> getAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String alertType,
            @RequestParam(required = false) String severity) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<Alert> alerts;
        if (status != null) {
            Alert.AlertStatus alertStatus = Alert.AlertStatus.valueOf(status.toUpperCase());
            alerts = alertRepository.findByStatus(alertStatus, pageable);
        } else if (alertType != null) {
            Alert.AlertType alertTypeEnum = Alert.AlertType.valueOf(alertType.toUpperCase());
            alerts = alertRepository.findByAlertType(alertTypeEnum, pageable);
        } else {
            alerts = alertRepository.findAll(pageable);
        }
        
        return ResponseEntity.ok(alerts);
    }
    
    @GetMapping("/alerts/{id}")
    public ResponseEntity<Alert> getAlert(@PathVariable Long id) {
        Optional<Alert> alert = alertRepository.findById(id);
        return alert.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/alerts/{id}/acknowledge")
    public ResponseEntity<Map<String, Object>> acknowledgeAlert(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = (User) authentication.getPrincipal();
            String note = request.get("note");
            
            advancedNotificationService.acknowledgeAlert(id, currentUser.getUsername(), note);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Alert acknowledged successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/alerts/{id}/resolve")
    public ResponseEntity<Map<String, Object>> resolveAlert(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = (User) authentication.getPrincipal();
            String resolution = request.get("resolution");
            String resolutionTypeStr = request.getOrDefault("resolutionType", "MANUAL");
            Alert.ResolutionType resolutionType = Alert.ResolutionType.valueOf(resolutionTypeStr);
            
            advancedNotificationService.resolveAlert(id, currentUser.getUsername(), resolution, resolutionType);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Alert resolved successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/configurations")
    public ResponseEntity<List<AlertConfiguration>> getAlertConfigurations() {
        List<AlertConfiguration> configurations = alertConfigurationRepository.findAll();
        return ResponseEntity.ok(configurations);
    }
    
    @GetMapping("/configurations/{id}")
    public ResponseEntity<AlertConfiguration> getAlertConfiguration(@PathVariable Long id) {
        Optional<AlertConfiguration> config = alertConfigurationRepository.findById(id);
        return config.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/configurations")
    public ResponseEntity<AlertConfiguration> createAlertConfiguration(@RequestBody AlertConfiguration configuration) {
        try {
            configuration.setCreatedAt(LocalDateTime.now());
            configuration.setUpdatedAt(LocalDateTime.now());
            AlertConfiguration saved = alertConfigurationRepository.save(configuration);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/configurations/{id}")
    public ResponseEntity<AlertConfiguration> updateAlertConfiguration(
            @PathVariable Long id,
            @RequestBody AlertConfiguration configuration) {
        try {
            Optional<AlertConfiguration> existing = alertConfigurationRepository.findById(id);
            if (existing.isPresent()) {
                AlertConfiguration config = existing.get();
                config.setAlertType(configuration.getAlertType());
                config.setEnabled(configuration.getEnabled());
                config.setNotificationChannels(configuration.getNotificationChannels());
                config.setSuppressionRules(configuration.getSuppressionRules());
                config.setUpdatedAt(LocalDateTime.now());
                
                AlertConfiguration saved = alertConfigurationRepository.save(config);
                return ResponseEntity.ok(saved);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/configurations/{id}")
    public ResponseEntity<Void> deleteAlertConfiguration(@PathVariable Long id) {
        try {
            alertConfigurationRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getAlertDashboardStats() {
        try {
            long totalAlerts = alertRepository.count();
            
            // Use the existing query methods to get counts
            List<Object[]> statusCounts = alertRepository.countAlertsByStatus();
            
            long newAlerts = 0;
            long acknowledgedAlerts = 0;
            long resolvedAlerts = 0;
            
            for (Object[] statusCount : statusCounts) {
                Alert.AlertStatus status = (Alert.AlertStatus) statusCount[0];
                Long count = (Long) statusCount[1];
                
                switch (status) {
                    case NEW -> newAlerts = count;
                    case ACKNOWLEDGED -> acknowledgedAlerts = count;
                    case RESOLVED -> resolvedAlerts = count;
                }
            }
            
            return ResponseEntity.ok(Map.of(
                "totalAlerts", totalAlerts,
                "newAlerts", newAlerts,
                "acknowledgedAlerts", acknowledgedAlerts,
                "resolvedAlerts", resolvedAlerts,
                "activeAlerts", newAlerts + acknowledgedAlerts
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }
}
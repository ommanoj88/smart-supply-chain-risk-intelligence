package com.smartsupplychain.riskintelligence.controller;

import com.smartsupplychain.riskintelligence.dto.ApiResponse;
import com.smartsupplychain.riskintelligence.enums.AlertSeverity;
import com.smartsupplychain.riskintelligence.model.Alert;
import com.smartsupplychain.riskintelligence.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/alerts")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<ApiResponse<List<Alert>>> getAllAlerts() {
        List<Alert> alerts = alertService.getAllAlerts();
        return ResponseEntity.ok(ApiResponse.success(alerts));
    }

    @GetMapping("/unacknowledged")
    public ResponseEntity<ApiResponse<List<Alert>>> getUnacknowledgedAlerts() {
        List<Alert> alerts = alertService.getUnacknowledgedAlerts();
        return ResponseEntity.ok(ApiResponse.success(alerts));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Alert>>> getAlertsByUser(@PathVariable Long userId) {
        List<Alert> alerts = alertService.getAlertsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(alerts));
    }

    @GetMapping("/user/{userId}/unacknowledged")
    public ResponseEntity<ApiResponse<List<Alert>>> getUnacknowledgedAlertsByUser(@PathVariable Long userId) {
        List<Alert> alerts = alertService.getUnacknowledgedAlertsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(alerts));
    }

    @GetMapping("/severity/{severity}")
    public ResponseEntity<ApiResponse<List<Alert>>> getAlertsBySeverity(@PathVariable AlertSeverity severity) {
        List<Alert> alerts = alertService.getAlertsBySeverity(severity);
        return ResponseEntity.ok(ApiResponse.success(alerts));
    }

    @GetMapping("/critical")
    public ResponseEntity<ApiResponse<List<Alert>>> getCriticalAlerts() {
        List<Alert> alerts = alertService.getCriticalAlerts();
        return ResponseEntity.ok(ApiResponse.success(alerts));
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<Alert>>> getAlertsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Alert> alerts = alertService.getAlertsByDateRange(start, end);
        return ResponseEntity.ok(ApiResponse.success(alerts));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<ApiResponse<Alert>> createAlert(
            @RequestParam Long userId,
            @RequestParam String message,
            @RequestParam AlertSeverity severity,
            @RequestParam(required = false) String referenceId,
            @RequestParam(required = false) String referenceType) {
        
        Alert alert;
        if (referenceId != null && referenceType != null) {
            alert = alertService.createAlert(userId, message, severity, referenceId, referenceType);
        } else {
            alert = alertService.createAlert(userId, message, severity);
        }
        
        return ResponseEntity.ok(ApiResponse.success("Alert created successfully", alert));
    }

    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> broadcastAlert(
            @RequestParam String message,
            @RequestParam AlertSeverity severity,
            @RequestParam(required = false) String referenceId,
            @RequestParam(required = false) String referenceType) {
        
        if (referenceId != null && referenceType != null) {
            alertService.broadcastAlert(message, severity, referenceId, referenceType);
        } else {
            alertService.broadcastAlert(message, severity);
        }
        
        return ResponseEntity.ok(ApiResponse.success("Alert broadcasted to all users"));
    }

    @PutMapping("/{alertId}/acknowledge")
    public ResponseEntity<ApiResponse<Alert>> acknowledgeAlert(@PathVariable Long alertId) {
        Alert alert = alertService.acknowledgeAlert(alertId);
        return ResponseEntity.ok(ApiResponse.success("Alert acknowledged", alert));
    }

    @PutMapping("/user/{userId}/acknowledge-all")
    public ResponseEntity<ApiResponse<String>> acknowledgeAllAlertsForUser(@PathVariable Long userId) {
        alertService.acknowledgeAllAlertsForUser(userId);
        return ResponseEntity.ok(ApiResponse.success("All alerts acknowledged for user"));
    }

    @DeleteMapping("/{alertId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteAlert(@PathVariable Long alertId) {
        alertService.deleteAlert(alertId);
        return ResponseEntity.ok(ApiResponse.success("Alert deleted successfully"));
    }

    @DeleteMapping("/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteOldAlerts(@RequestParam(defaultValue = "30") int daysOld) {
        alertService.deleteOldAlerts(daysOld);
        return ResponseEntity.ok(ApiResponse.success("Old alerts cleaned up"));
    }
}
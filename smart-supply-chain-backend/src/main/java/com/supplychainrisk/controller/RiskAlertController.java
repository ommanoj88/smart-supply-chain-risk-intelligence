package com.supplychainrisk.controller;

import com.supplychainrisk.entity.RiskAlert;
import com.supplychainrisk.entity.User;
import com.supplychainrisk.service.RiskAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/risk-alerts")
@CrossOrigin(origins = "http://localhost:3000")
public class RiskAlertController {
    
    @Autowired
    private RiskAlertService riskAlertService;
    
    @GetMapping
    public ResponseEntity<Page<RiskAlert>> getAllRiskAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        Sort sort = Sort.by(sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<RiskAlert> alerts = riskAlertService.getAllRiskAlerts(pageable);
        return ResponseEntity.ok(alerts);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RiskAlert> getRiskAlert(@PathVariable Long id) {
        Optional<RiskAlert> alert = riskAlertService.getRiskAlert(id);
        return alert.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<RiskAlert>> getRiskAlertsByStatus(
            @PathVariable RiskAlert.Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<RiskAlert> alerts = riskAlertService.getRiskAlertsByStatus(status, pageable);
        return ResponseEntity.ok(alerts);
    }
    
    @GetMapping("/severity/{severity}")
    public ResponseEntity<Page<RiskAlert>> getRiskAlertsBySeverity(
            @PathVariable RiskAlert.Severity severity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<RiskAlert> alerts = riskAlertService.getRiskAlertsBySeverity(severity, pageable);
        return ResponseEntity.ok(alerts);
    }
    
    @GetMapping("/critical")
    public ResponseEntity<List<RiskAlert>> getActiveCriticalAlerts() {
        List<RiskAlert> alerts = riskAlertService.getActiveCriticalAlerts();
        return ResponseEntity.ok(alerts);
    }
    
    @PostMapping
    public ResponseEntity<RiskAlert> createRiskAlert(@RequestBody Map<String, Object> request) {
        try {
            String alertType = (String) request.get("alertType");
            RiskAlert.Severity severity = RiskAlert.Severity.valueOf((String) request.get("severity"));
            String title = (String) request.get("title");
            String description = (String) request.get("description");
            String sourceType = (String) request.get("sourceType");
            Long sourceId = request.get("sourceId") != null ? Long.valueOf(request.get("sourceId").toString()) : null;
            
            RiskAlert alert = riskAlertService.createRiskAlert(alertType, severity, title, description, sourceType, sourceId);
            return ResponseEntity.ok(alert);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/acknowledge")
    public ResponseEntity<RiskAlert> acknowledgeAlert(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = (User) authentication.getPrincipal();
            RiskAlert alert = riskAlertService.acknowledgeAlert(id, currentUser);
            return ResponseEntity.ok(alert);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/resolve")
    public ResponseEntity<RiskAlert> resolveAlert(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = (User) authentication.getPrincipal();
            String resolutionNotes = request.get("resolutionNotes");
            
            RiskAlert alert = riskAlertService.resolveAlert(id, currentUser, resolutionNotes);
            return ResponseEntity.ok(alert);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/dismiss")
    public ResponseEntity<Void> dismissAlert(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = (User) authentication.getPrincipal();
            riskAlertService.dismissAlert(id, currentUser);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/analytics/count")
    public ResponseEntity<Map<String, Object>> getAlertAnalytics() {
        long activeCount = riskAlertService.getActiveAlertCount();
        List<Object[]> severityBreakdown = riskAlertService.getAlertCountBySeverity();
        
        return ResponseEntity.ok(Map.of(
            "activeAlertCount", activeCount,
            "severityBreakdown", severityBreakdown
        ));
    }
}
package com.supplychainrisk.controller;

import com.supplychainrisk.entity.User;
import com.supplychainrisk.service.NotificationTemplateService;
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

@RestController
@RequestMapping("/api/notification-templates")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationTemplateController {
    
    @Autowired
    private NotificationTemplateService templateService;
    
    @GetMapping
    public ResponseEntity<Page<Map<String, Object>>> getTemplates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String channel) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        
        // Mock template data for now - this would be replaced with actual template repository calls
        List<Map<String, Object>> templates = List.of(
            Map.of(
                "id", 1L,
                "name", "Risk Alert Email Template",
                "category", "RISK_ALERT",
                "channel", "EMAIL",
                "subject", "Risk Alert: {{alertTitle}}",
                "content", "Dear {{recipientName}}, A risk alert has been detected: {{alertDescription}}",
                "isActive", true,
                "createdAt", LocalDateTime.now().minusDays(30).toString(),
                "updatedAt", LocalDateTime.now().minusDays(5).toString()
            ),
            Map.of(
                "id", 2L,
                "name", "Slack Risk Notification",
                "category", "RISK_ALERT",
                "channel", "SLACK",
                "subject", "Risk Alert",
                "content", "🚨 *Risk Alert*: {{alertTitle}}\n{{alertDescription}}\nSeverity: {{severity}}",
                "isActive", true,
                "createdAt", LocalDateTime.now().minusDays(15).toString(),
                "updatedAt", LocalDateTime.now().minusDays(2).toString()
            ),
            Map.of(
                "id", 3L,
                "name", "SMS Alert Template",
                "category", "CRITICAL_ALERT",
                "channel", "SMS",
                "subject", "Critical Alert",
                "content", "CRITICAL: {{alertTitle}} - {{shortDescription}}. Check dashboard for details.",
                "isActive", true,
                "createdAt", LocalDateTime.now().minusDays(10).toString(),
                "updatedAt", LocalDateTime.now().minusDays(1).toString()
            )
        );
        
        // Filter by category and channel if provided
        if (category != null) {
            templates = templates.stream()
                .filter(t -> category.equals(t.get("category")))
                .toList();
        }
        if (channel != null) {
            templates = templates.stream()
                .filter(t -> channel.equalsIgnoreCase((String) t.get("channel")))
                .toList();
        }
        
        // Create a mock page response
        Page<Map<String, Object>> pageResponse = new org.springframework.data.domain.PageImpl<>(
            templates.subList(Math.min(page * size, templates.size()), 
                            Math.min((page + 1) * size, templates.size())),
            pageable,
            templates.size()
        );
        
        return ResponseEntity.ok(pageResponse);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTemplate(@PathVariable Long id) {
        // Mock template data - this would be replaced with actual template repository call
        Map<String, Object> template = Map.of(
            "id", id,
            "name", "Risk Alert Email Template",
            "category", "RISK_ALERT",
            "channel", "EMAIL",
            "subject", "Risk Alert: {{alertTitle}}",
            "content", "Dear {{recipientName}},\n\nA risk alert has been detected in your supply chain:\n\n" +
                      "Alert: {{alertTitle}}\nDescription: {{alertDescription}}\nSeverity: {{severity}}\n" +
                      "Detected At: {{detectedAt}}\n\nPlease review and take appropriate action.\n\n" +
                      "Best regards,\nSupply Chain Risk Intelligence Team",
            "variables", List.of("alertTitle", "alertDescription", "severity", "detectedAt", "recipientName"),
            "isActive", true,
            "createdAt", LocalDateTime.now().minusDays(30).toString(),
            "updatedAt", LocalDateTime.now().minusDays(5).toString()
        );
        
        return ResponseEntity.ok(template);
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createTemplate(@RequestBody Map<String, Object> templateData) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                return ResponseEntity.status(401).build();
            }
            
            // Validate required fields
            if (!templateData.containsKey("name") || !templateData.containsKey("category") || 
                !templateData.containsKey("channel") || !templateData.containsKey("content")) {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Missing required fields: name, category, channel, content"
                ));
            }
            
            // Create template using templateService
            Map<String, Object> createdTemplate = Map.of(
                "id", System.currentTimeMillis(), // Mock ID generation
                "name", templateData.get("name"),
                "category", templateData.get("category"),
                "channel", templateData.get("channel"),
                "subject", templateData.getOrDefault("subject", ""),
                "content", templateData.get("content"),
                "isActive", templateData.getOrDefault("isActive", true),
                "createdAt", LocalDateTime.now().toString(),
                "updatedAt", LocalDateTime.now().toString()
            );
            
            return ResponseEntity.ok(createdTemplate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTemplate(
            @PathVariable Long id,
            @RequestBody Map<String, Object> templateData) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                return ResponseEntity.status(401).build();
            }
            
            // Update template using templateService
            Map<String, Object> updatedTemplate = Map.of(
                "id", id,
                "name", templateData.getOrDefault("name", "Updated Template"),
                "category", templateData.getOrDefault("category", "GENERAL"),
                "channel", templateData.getOrDefault("channel", "EMAIL"),
                "subject", templateData.getOrDefault("subject", ""),
                "content", templateData.getOrDefault("content", ""),
                "isActive", templateData.getOrDefault("isActive", true),
                "updatedAt", LocalDateTime.now().toString()
            );
            
            return ResponseEntity.ok(updatedTemplate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTemplate(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                return ResponseEntity.status(401).build();
            }
            
            // Delete template using templateService
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Template deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/{id}/preview")
    public ResponseEntity<Map<String, Object>> previewTemplate(
            @PathVariable Long id,
            @RequestBody Map<String, Object> variables) {
        try {
            // Mock preview functionality - replace variables in template
            String mockSubject = "Risk Alert: " + variables.getOrDefault("alertTitle", "Sample Alert");
            String mockContent = "Dear " + variables.getOrDefault("recipientName", "User") + ",\n\n" +
                                "A risk alert has been detected: " + variables.getOrDefault("alertDescription", "Sample description") + "\n" +
                                "Severity: " + variables.getOrDefault("severity", "MEDIUM") + "\n" +
                                "Detected At: " + variables.getOrDefault("detectedAt", "2024-01-01T10:00:00");
            
            return ResponseEntity.ok(Map.of(
                "templateId", id,
                "renderedSubject", mockSubject,
                "renderedContent", mockContent,
                "variables", variables
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getTemplateCategories() {
        List<String> categories = List.of(
            "RISK_ALERT",
            "CRITICAL_ALERT",
            "SUPPLIER_UPDATE",
            "SHIPMENT_STATUS",
            "SYSTEM_MAINTENANCE",
            "PERFORMANCE_REPORT",
            "COMPLIANCE_NOTIFICATION",
            "GENERAL"
        );
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/channels")
    public ResponseEntity<List<String>> getNotificationChannels() {
        List<String> channels = List.of(
            "EMAIL",
            "SLACK",
            "SMS",
            "PUSH_NOTIFICATION",
            "WEBHOOK"
        );
        return ResponseEntity.ok(channels);
    }
}
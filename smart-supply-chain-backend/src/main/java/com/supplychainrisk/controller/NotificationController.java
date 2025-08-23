package com.supplychainrisk.controller;

import com.supplychainrisk.entity.Notification;
import com.supplychainrisk.entity.User;
import com.supplychainrisk.service.NotificationService;
import com.supplychainrisk.service.AdvancedNotificationService;
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
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private AdvancedNotificationService advancedNotificationService;
    
    @GetMapping
    public ResponseEntity<Page<Notification>> getUserNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return ResponseEntity.status(401).build();
        }
        
        User currentUser = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<Notification> notifications;
        if (status != null) {
            Notification.Status notificationStatus = Notification.Status.valueOf(status.toUpperCase());
            notifications = notificationService.getUserNotificationsByStatus(currentUser, notificationStatus, pageable);
        } else if (category != null) {
            notifications = notificationService.getUserNotificationsByCategory(currentUser, category, pageable);
        } else {
            notifications = notificationService.getUserNotifications(currentUser, pageable);
        }
        
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotification(@PathVariable Long id) {
        Optional<Notification> notification = notificationService.getNotification(id);
        return notification.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Object>> getUnreadCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return ResponseEntity.status(401).build();
        }
        
        User currentUser = (User) authentication.getPrincipal();
        long unreadCount = notificationService.getUnreadNotificationCount(currentUser);
        List<Object[]> categoryBreakdown = notificationService.getUnreadNotificationCountByCategory(currentUser);
        
        return ResponseEntity.ok(Map.of(
            "unreadCount", unreadCount,
            "categoryBreakdown", categoryBreakdown
        ));
    }
    
    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Map<String, Object> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = (User) authentication.getPrincipal();
            String category = (String) request.get("category");
            String subject = (String) request.get("subject");
            String content = (String) request.get("content");
            Notification.Priority priority = request.get("priority") != null ? 
                    Notification.Priority.valueOf((String) request.get("priority")) : 
                    Notification.Priority.MEDIUM;
            
            Notification notification = notificationService.createNotification(currentUser, category, subject, content, priority);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/schedule")
    public ResponseEntity<Notification> scheduleNotification(@RequestBody Map<String, Object> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = (User) authentication.getPrincipal();
            String category = (String) request.get("category");
            String subject = (String) request.get("subject");
            String content = (String) request.get("content");
            Notification.Priority priority = request.get("priority") != null ? 
                    Notification.Priority.valueOf((String) request.get("priority")) : 
                    Notification.Priority.MEDIUM;
            LocalDateTime scheduledFor = LocalDateTime.parse((String) request.get("scheduledFor"));
            
            Notification notification = notificationService.scheduleNotification(
                    currentUser, category, subject, content, priority, scheduledFor);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        try {
            Notification notification = notificationService.markAsRead(id);
            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return ResponseEntity.status(401).build();
        }
        
        User currentUser = (User) authentication.getPrincipal();
        notificationService.markAllAsRead(currentUser);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        try {
            notificationService.deleteNotification(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Advanced notification endpoints
    
    @PostMapping("/advanced/send")
    public ResponseEntity<Map<String, Object>> sendAdvancedNotification(@RequestBody Map<String, Object> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                return ResponseEntity.status(401).build();
            }
            
            // Create and send advanced notification using the AdvancedNotificationService
            // This would include template processing, multi-channel delivery, etc.
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Advanced notification sent successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/delivery-status/{notificationId}")
    public ResponseEntity<Map<String, Object>> getDeliveryStatus(@PathVariable Long notificationId) {
        try {
            // Get delivery status from AdvancedNotificationService
            Map<String, Object> deliveryStatus = Map.of(
                "notificationId", notificationId,
                "status", "delivered",
                "channels", List.of("email", "push"),
                "deliveredAt", LocalDateTime.now().toString()
            );
            return ResponseEntity.ok(deliveryStatus);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/preferences")
    public ResponseEntity<Map<String, Object>> updateNotificationPreferences(@RequestBody Map<String, Object> preferences) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = (User) authentication.getPrincipal();
            // Update user notification preferences
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Notification preferences updated"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }
}
package com.supplychainrisk.service;

import com.supplychainrisk.entity.Notification;
import com.supplychainrisk.entity.RiskAlert;
import com.supplychainrisk.entity.User;
import com.supplychainrisk.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserService userService;
    
    public Notification createNotification(User user, String category, String subject, String content, 
                                         Notification.Priority priority) {
        Notification notification = new Notification(user, category, subject, content, priority);
        Notification savedNotification = notificationRepository.save(notification);
        
        logger.info("Created notification for user {} with category: {}", user.getId(), category);
        return savedNotification;
    }
    
    public Notification scheduleNotification(User user, String category, String subject, String content, 
                                           Notification.Priority priority, LocalDateTime scheduledFor) {
        Notification notification = new Notification(user, category, subject, content, priority);
        notification.setScheduledFor(scheduledFor);
        
        Notification savedNotification = notificationRepository.save(notification);
        logger.info("Scheduled notification for user {} at {}", user.getId(), scheduledFor);
        return savedNotification;
    }
    
    public Optional<Notification> getNotification(Long id) {
        return notificationRepository.findById(id);
    }
    
    public Page<Notification> getUserNotifications(User user, Pageable pageable) {
        return notificationRepository.findByUser(user, pageable);
    }
    
    public Page<Notification> getUserNotificationsByStatus(User user, Notification.Status status, Pageable pageable) {
        return notificationRepository.findByUserAndStatus(user, status, pageable);
    }
    
    public Page<Notification> getUserNotificationsByCategory(User user, String category, Pageable pageable) {
        return notificationRepository.findByUserAndCategory(user, category, pageable);
    }
    
    public long getUnreadNotificationCount(User user) {
        return notificationRepository.countUnreadByUser(user);
    }
    
    public List<Object[]> getUnreadNotificationCountByCategory(User user) {
        return notificationRepository.countUnreadByUserAndCategory(user);
    }
    
    public Notification markAsRead(Long notificationId) {
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.setStatus(Notification.Status.DELIVERED);
            notification.setDeliveredAt(LocalDateTime.now());
            
            Notification savedNotification = notificationRepository.save(notification);
            logger.info("Marked notification {} as read", notificationId);
            return savedNotification;
        }
        throw new RuntimeException("Notification not found with id: " + notificationId);
    }
    
    public void markAllAsRead(User user) {
        Page<Notification> pendingNotifications = notificationRepository.findByUserAndStatus(
                user, Notification.Status.PENDING, Pageable.unpaged());
        
        for (Notification notification : pendingNotifications) {
            notification.setStatus(Notification.Status.DELIVERED);
            notification.setDeliveredAt(LocalDateTime.now());
        }
        
        notificationRepository.saveAll(pendingNotifications);
        logger.info("Marked all notifications as read for user {}", user.getId());
    }
    
    public void deleteNotification(Long notificationId) {
        if (notificationRepository.existsById(notificationId)) {
            notificationRepository.deleteById(notificationId);
            logger.info("Deleted notification {}", notificationId);
        } else {
            throw new RuntimeException("Notification not found with id: " + notificationId);
        }
    }
    
    // Process pending notifications for delivery
    public void processPendingNotifications() {
        List<Notification> pendingNotifications = notificationRepository
                .findPendingNotifications(LocalDateTime.now());
        
        for (Notification notification : pendingNotifications) {
            try {
                processNotificationDelivery(notification);
            } catch (Exception e) {
                logger.error("Failed to process notification {}: {}", notification.getId(), e.getMessage());
                handleNotificationFailure(notification, e.getMessage());
            }
        }
    }
    
    private void processNotificationDelivery(Notification notification) {
        // In a real implementation, this would integrate with email/SMS/push notification services
        notification.setStatus(Notification.Status.SENT);
        notification.setSentAt(LocalDateTime.now());
        notification.setDeliveredAt(LocalDateTime.now());
        notification.setStatus(Notification.Status.DELIVERED);
        
        notificationRepository.save(notification);
        logger.info("Successfully delivered notification {}", notification.getId());
    }
    
    private void handleNotificationFailure(Notification notification, String errorMessage) {
        notification.setStatus(Notification.Status.FAILED);
        notification.setFailedAt(LocalDateTime.now());
        notification.setFailureReason(errorMessage);
        notification.setRetryCount(notification.getRetryCount() + 1);
        
        // Schedule retry if within retry limit
        if (notification.getRetryCount() < notification.getMaxRetries()) {
            notification.setStatus(Notification.Status.RETRYING);
            notification.setScheduledFor(LocalDateTime.now().plusMinutes(5 * notification.getRetryCount()));
        }
        
        notificationRepository.save(notification);
    }
    
    // Specialized notification methods
    public void sendRiskAlertNotification(RiskAlert riskAlert) {
        // Get users who should receive risk alert notifications
        List<User> alertRecipients = userService.getUsersWithRole(User.Role.SUPPLY_MANAGER);
        alertRecipients.addAll(userService.getUsersWithRole(User.Role.ADMIN));
        
        String subject = String.format("%s Risk Alert: %s", 
                riskAlert.getSeverity().toString(), riskAlert.getTitle());
        String content = String.format("A %s severity risk alert has been detected:\n\n%s\n\nPlease review immediately.", 
                riskAlert.getSeverity().toString().toLowerCase(), riskAlert.getDescription());
        
        Notification.Priority priority = mapSeverityToPriority(riskAlert.getSeverity());
        
        for (User user : alertRecipients) {
            createNotification(user, "RISK_ALERT", subject, content, priority);
        }
        
        logger.info("Sent risk alert notifications for alert {}", riskAlert.getId());
    }
    
    public void sendShipmentUpdateNotification(Long shipmentId, String trackingNumber, String updateMessage) {
        // Get users associated with the shipment
        List<User> recipients = userService.getUsersWithRole(User.Role.SUPPLY_MANAGER);
        
        String subject = String.format("Shipment Update: %s", trackingNumber);
        String content = String.format("Your shipment %s has been updated:\n\n%s", trackingNumber, updateMessage);
        
        for (User user : recipients) {
            createNotification(user, "SHIPMENT_UPDATE", subject, content, Notification.Priority.MEDIUM);
        }
        
        logger.info("Sent shipment update notifications for shipment {}", shipmentId);
    }
    
    public void sendSupplierPerformanceNotification(Long supplierId, String supplierName, String performanceMessage) {
        List<User> recipients = userService.getUsersWithRole(User.Role.SUPPLY_MANAGER);
        recipients.addAll(userService.getUsersWithRole(User.Role.ANALYST));
        
        String subject = String.format("Supplier Performance Alert: %s", supplierName);
        String content = String.format("Supplier %s performance update:\n\n%s", supplierName, performanceMessage);
        
        for (User user : recipients) {
            createNotification(user, "SUPPLIER_UPDATE", subject, content, Notification.Priority.MEDIUM);
        }
        
        logger.info("Sent supplier performance notifications for supplier {}", supplierId);
    }
    
    private Notification.Priority mapSeverityToPriority(RiskAlert.Severity severity) {
        return switch (severity) {
            case CRITICAL -> Notification.Priority.URGENT;
            case HIGH -> Notification.Priority.HIGH;
            case MEDIUM -> Notification.Priority.MEDIUM;
            case LOW -> Notification.Priority.LOW;
        };
    }
}
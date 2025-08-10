package com.smartsupplychain.riskintelligence.service;

import com.smartsupplychain.riskintelligence.enums.AlertSeverity;
import com.smartsupplychain.riskintelligence.model.Alert;
import com.smartsupplychain.riskintelligence.model.User;
import com.smartsupplychain.riskintelligence.repository.AlertRepository;
import com.smartsupplychain.riskintelligence.repository.UserRepository;
import com.smartsupplychain.riskintelligence.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@smartsupplychain.com}")
    private String fromEmail;

    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    public List<Alert> getUnacknowledgedAlerts() {
        return alertRepository.findByIsAcknowledgedFalse();
    }

    public List<Alert> getAlertsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return alertRepository.findByUser(user);
    }

    public List<Alert> getUnacknowledgedAlertsByUser(Long userId) {
        return alertRepository.findUnacknowledgedByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Alert> getAlertsBySeverity(AlertSeverity severity) {
        return alertRepository.findBySeverity(severity);
    }

    public List<Alert> getCriticalAlerts() {
        List<AlertSeverity> criticalSeverities = Arrays.asList(AlertSeverity.CRITICAL, AlertSeverity.ERROR);
        return alertRepository.findUnacknowledgedBySeveritiesOrderByCreatedAtDesc(criticalSeverities);
    }

    public List<Alert> getAlertsByDateRange(LocalDateTime start, LocalDateTime end) {
        return alertRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(start, end);
    }

    public Alert createAlert(Long userId, String message, AlertSeverity severity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Alert alert = new Alert(user, message, severity);
        alert = alertRepository.save(alert);
        
        // Send email notification for critical alerts
        if (severity == AlertSeverity.CRITICAL || severity == AlertSeverity.ERROR) {
            sendEmailNotification(alert);
        }
        
        return alert;
    }

    public Alert createAlert(Long userId, String message, AlertSeverity severity, String referenceId, String referenceType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Alert alert = new Alert(user, message, severity, referenceId, referenceType);
        alert = alertRepository.save(alert);
        
        // Send email notification for critical alerts
        if (severity == AlertSeverity.CRITICAL || severity == AlertSeverity.ERROR) {
            sendEmailNotification(alert);
        }
        
        return alert;
    }

    public void broadcastAlert(String message, AlertSeverity severity) {
        List<User> activeUsers = userRepository.findByIsActiveTrue();
        
        for (User user : activeUsers) {
            Alert alert = new Alert(user, message, severity);
            alertRepository.save(alert);
            
            // Send email notification for critical alerts
            if (severity == AlertSeverity.CRITICAL || severity == AlertSeverity.ERROR) {
                sendEmailNotification(alert);
            }
        }
    }

    public void broadcastAlert(String message, AlertSeverity severity, String referenceId, String referenceType) {
        List<User> activeUsers = userRepository.findByIsActiveTrue();
        
        for (User user : activeUsers) {
            Alert alert = new Alert(user, message, severity, referenceId, referenceType);
            alertRepository.save(alert);
            
            // Send email notification for critical alerts
            if (severity == AlertSeverity.CRITICAL || severity == AlertSeverity.ERROR) {
                sendEmailNotification(alert);
            }
        }
    }

    public Alert acknowledgeAlert(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert", "id", alertId));
        
        alert.setIsAcknowledged(true);
        alert.setAcknowledgedAt(LocalDateTime.now());
        
        return alertRepository.save(alert);
    }

    public void acknowledgeAllAlertsForUser(Long userId) {
        List<Alert> unacknowledgedAlerts = alertRepository.findUnacknowledgedByUserIdOrderByCreatedAtDesc(userId);
        
        for (Alert alert : unacknowledgedAlerts) {
            alert.setIsAcknowledged(true);
            alert.setAcknowledgedAt(LocalDateTime.now());
            alertRepository.save(alert);
        }
    }

    public void deleteAlert(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert", "id", alertId));
        alertRepository.delete(alert);
    }

    public void deleteOldAlerts(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        List<Alert> oldAlerts = alertRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(
                LocalDateTime.now().minusYears(10), cutoffDate);
        
        // Only delete acknowledged alerts
        oldAlerts.stream()
                .filter(Alert::getIsAcknowledged)
                .forEach(alertRepository::delete);
    }

    private void sendEmailNotification(Alert alert) {
        if (mailSender == null) {
            // Mail sender not configured
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(alert.getUser().getEmail());
            message.setFrom(fromEmail);
            message.setSubject("Smart Supply Chain Alert - " + alert.getSeverity());
            
            StringBuilder body = new StringBuilder();
            body.append("Dear ").append(alert.getUser().getName()).append(",\n\n");
            body.append("You have received a new alert:\n\n");
            body.append("Severity: ").append(alert.getSeverity()).append("\n");
            body.append("Message: ").append(alert.getMessage()).append("\n");
            body.append("Time: ").append(alert.getCreatedAt()).append("\n");
            
            if (alert.getReferenceType() != null && alert.getReferenceId() != null) {
                body.append("Reference: ").append(alert.getReferenceType())
                    .append(" (ID: ").append(alert.getReferenceId()).append(")\n");
            }
            
            body.append("\nPlease log in to the Smart Supply Chain platform to view more details.\n\n");
            body.append("Best regards,\n");
            body.append("Smart Supply Chain Risk Intelligence Team");
            
            message.setText(body.toString());
            
            mailSender.send(message);
        } catch (Exception e) {
            // Log the error but don't fail the alert creation
            System.err.println("Failed to send email notification: " + e.getMessage());
        }
    }
}
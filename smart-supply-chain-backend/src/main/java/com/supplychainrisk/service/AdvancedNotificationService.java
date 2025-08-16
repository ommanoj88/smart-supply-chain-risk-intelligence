package com.supplychainrisk.service;

import com.supplychainrisk.entity.*;
import com.supplychainrisk.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class AdvancedNotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AdvancedNotificationService.class);
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private AlertRepository alertRepository;
    
    @Autowired
    private AlertConfigurationRepository alertConfigurationRepository;
    
    @Autowired
    private NotificationDeliveryRepository notificationDeliveryRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SlackService slackService;
    
    @Autowired
    private PushNotificationService pushNotificationService;
    
    @Autowired
    private SMSService smsService;
    
    @Autowired
    private NotificationTemplateService templateService;
    
    @Autowired
    private UserService userService;
    
    public void sendAlert(Alert alert) {
        logger.info("Processing alert notification for alert ID: {}", alert.getId());
        
        try {
            // Get alert configuration
            AlertConfiguration config = getAlertConfiguration(alert.getAlertType());
            
            // Check if alert should be sent (suppression rules, business hours, etc.)
            if (!shouldSendAlert(alert, config)) {
                logger.info("Alert suppressed based on configuration: {}", alert.getId());
                return;
            }
            
            // Get recipients based on configuration and user preferences
            Set<NotificationRecipient> recipients = getNotificationRecipients(alert, config);
            
            // Generate notification content
            NotificationContent content = generateNotificationContent(alert, config);
            
            // Create notification record
            Notification notification = createNotificationRecord(alert, content, recipients);
            
            // Send via configured channels
            for (Notification.NotificationChannel channel : config.getNotificationChannels()) {
                sendViaChannel(notification, channel, recipients, content);
            }
            
            // Schedule escalation if required
            scheduleEscalation(alert, config);
            
            logger.info("Alert notification sent successfully for alert ID: {}", alert.getId());
            
        } catch (Exception e) {
            logger.error("Failed to send alert notification for alert ID: {}", alert.getId(), e);
            handleNotificationFailure(alert, e);
        }
    }
    
    public void sendNotification(NotificationRequest request) {
        logger.info("Sending custom notification: {}", request.getTitle());
        
        try {
            // Validate request
            validateNotificationRequest(request);
            
            // Get recipients with preferences
            Set<NotificationRecipient> recipients = resolveRecipients(request.getRecipients());
            
            // Generate content from template or custom content
            NotificationContent content = generateCustomNotificationContent(request);
            
            // Create notification record
            Notification notification = createCustomNotificationRecord(request, content, recipients);
            
            // Send via requested channels
            for (Notification.NotificationChannel channel : request.getChannels()) {
                sendViaChannel(notification, channel, recipients, content);
            }
            
            // Track delivery
            trackNotificationDelivery(notification);
            
            logger.info("Custom notification sent successfully: {}", notification.getId());
            
        } catch (Exception e) {
            logger.error("Failed to send custom notification: {}", request.getTitle(), e);
            throw new NotificationException("Failed to send notification", e);
        }
    }
    
    public void acknowledgeAlert(Long alertId, String userId, String note) {
        Alert alert = alertRepository.findById(alertId)
            .orElseThrow(() -> new AlertNotFoundException("Alert not found: " + alertId));
        
        if (alert.getStatus() == Alert.AlertStatus.NEW) {
            alert.setStatus(Alert.AlertStatus.ACKNOWLEDGED);
            alert.setAcknowledgedAt(LocalDateTime.now());
            alert.setAcknowledgedBy(userId);
            alert.setAcknowledgmentNote(note);
            
            alertRepository.save(alert);
            
            // Cancel escalation
            cancelEscalation(alert);
            
            // Send acknowledgment notification
            sendAcknowledgmentNotification(alert, userId);
            
            logger.info("Alert acknowledged by user {}: {}", userId, alertId);
        }
    }
    
    public void resolveAlert(Long alertId, String userId, String resolution, Alert.ResolutionType type) {
        Alert alert = alertRepository.findById(alertId)
            .orElseThrow(() -> new AlertNotFoundException("Alert not found: " + alertId));
        
        alert.setStatus(Alert.AlertStatus.RESOLVED);
        alert.setResolvedAt(LocalDateTime.now());
        alert.setResolvedBy(userId);
        alert.setResolutionNote(resolution);
        alert.setResolutionType(type);
        
        alertRepository.save(alert);
        
        // Send resolution notification
        sendResolutionNotification(alert, userId);
        
        // Update metrics
        updateAlertMetrics(alert);
        
        logger.info("Alert resolved by user {}: {}", userId, alertId);
    }
    
    private void sendViaChannel(Notification notification, Notification.NotificationChannel channel,
                               Set<NotificationRecipient> recipients, NotificationContent content) {
        
        switch (channel) {
            case EMAIL:
                sendEmailNotifications(notification, recipients, content);
                break;
            case SLACK:
                sendSlackNotifications(notification, recipients, content);
                break;
            case PUSH:
                sendPushNotifications(notification, recipients, content);
                break;
            case SMS:
                sendSMSNotifications(notification, recipients, content);
                break;
            case VOICE:
                sendVoiceNotifications(notification, recipients, content);
                break;
        }
    }
    
    private void sendEmailNotifications(Notification notification, Set<NotificationRecipient> recipients,
                                       NotificationContent content) {
        
        for (NotificationRecipient recipient : recipients) {
            if (recipient.getEmailAddress() != null && recipient.isEmailEnabled()) {
                try {
                    EmailMessage email = EmailMessage.builder()
                        .to(recipient.getEmailAddress())
                        .subject(content.getEmailSubject())
                        .htmlContent(content.getEmailHtmlContent())
                        .textContent(content.getEmailTextContent())
                        .templateId(content.getEmailTemplateId())
                        .templateData(content.getTemplateData())
                        .build();
                    
                    String messageId = emailService.sendEmail(email);
                    
                    // Track delivery
                    recordDelivery(notification, Notification.NotificationChannel.EMAIL, 
                                 recipient.getUserId(), messageId, NotificationDelivery.DeliveryStatus.SENT);
                    
                } catch (Exception e) {
                    logger.error("Failed to send email to {}", recipient.getEmailAddress(), e);
                    recordDelivery(notification, Notification.NotificationChannel.EMAIL, 
                                 recipient.getUserId(), null, NotificationDelivery.DeliveryStatus.FAILED);
                }
            }
        }
    }
    
    private void sendSlackNotifications(Notification notification, Set<NotificationRecipient> recipients,
                                       NotificationContent content) {
        
        for (NotificationRecipient recipient : recipients) {
            if (recipient.getSlackChannel() != null && recipient.isSlackEnabled()) {
                try {
                    SlackMessage message = SlackMessage.builder()
                        .channel(recipient.getSlackChannel())
                        .text(content.getSlackText())
                        .attachments(content.getSlackAttachments())
                        .blocks(content.getSlackBlocks())
                        .build();
                    
                    String messageId = slackService.sendMessage(message);
                    
                    // Track delivery
                    recordDelivery(notification, Notification.NotificationChannel.SLACK, 
                                 recipient.getUserId(), messageId, NotificationDelivery.DeliveryStatus.SENT);
                    
                } catch (Exception e) {
                    logger.error("Failed to send Slack message to {}", recipient.getSlackChannel(), e);
                    recordDelivery(notification, Notification.NotificationChannel.SLACK, 
                                 recipient.getUserId(), null, NotificationDelivery.DeliveryStatus.FAILED);
                }
            }
        }
    }
    
    @Scheduled(fixedRate = 60000) // Every minute
    public void processEscalations() {
        try {
            List<Alert> alertsForEscalation = getAlertsForEscalation();
            
            for (Alert alert : alertsForEscalation) {
                escalateAlert(alert);
            }
            
        } catch (Exception e) {
            logger.error("Error processing escalations", e);
        }
    }
    
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void processNotificationDeliveryTracking() {
        try {
            // Check email delivery status
            updateEmailDeliveryStatus();
            
            // Check push notification delivery status
            updatePushNotificationStatus();
            
            // Process read receipts
            processReadReceipts();
            
        } catch (Exception e) {
            logger.error("Error processing notification delivery tracking", e);
        }
    }
    
    // Private helper methods (to be implemented)
    private AlertConfiguration getAlertConfiguration(Alert.AlertType alertType) {
        List<AlertConfiguration> configs = alertConfigurationRepository.findByAlertTypeAndEnabledTrue(alertType);
        return configs.isEmpty() ? null : configs.get(0);
    }
    
    private boolean shouldSendAlert(Alert alert, AlertConfiguration config) {
        // Implementation for suppression rules, business hours, etc.
        return true; // Simplified for now
    }
    
    private Set<NotificationRecipient> getNotificationRecipients(Alert alert, AlertConfiguration config) {
        // Implementation to resolve recipients based on configuration
        return Set.of(); // Simplified for now
    }
    
    private NotificationContent generateNotificationContent(Alert alert, AlertConfiguration config) {
        // Implementation to generate rich notification content
        return new NotificationContent(); // Simplified for now
    }
    
    private Notification createNotificationRecord(Alert alert, NotificationContent content, Set<NotificationRecipient> recipients) {
        // Implementation to create notification record
        return new Notification(); // Simplified for now
    }
    
    private void scheduleEscalation(Alert alert, AlertConfiguration config) {
        // Implementation for escalation scheduling
    }
    
    private void handleNotificationFailure(Alert alert, Exception e) {
        // Implementation for failure handling
    }
    
    private void validateNotificationRequest(NotificationRequest request) {
        // Implementation for request validation
    }
    
    private Set<NotificationRecipient> resolveRecipients(Set<String> recipients) {
        // Implementation to resolve recipient details
        return Set.of(); // Simplified for now
    }
    
    private NotificationContent generateCustomNotificationContent(NotificationRequest request) {
        // Implementation for custom content generation
        return new NotificationContent(); // Simplified for now
    }
    
    private Notification createCustomNotificationRecord(NotificationRequest request, NotificationContent content, Set<NotificationRecipient> recipients) {
        // Implementation for custom notification creation
        return new Notification(); // Simplified for now
    }
    
    private void trackNotificationDelivery(Notification notification) {
        // Implementation for delivery tracking
    }
    
    private void cancelEscalation(Alert alert) {
        // Implementation to cancel escalation
    }
    
    private void sendAcknowledgmentNotification(Alert alert, String userId) {
        // Implementation to send acknowledgment notification
    }
    
    private void sendResolutionNotification(Alert alert, String userId) {
        // Implementation to send resolution notification
    }
    
    private void updateAlertMetrics(Alert alert) {
        // Implementation to update metrics
    }
    
    private void sendPushNotifications(Notification notification, Set<NotificationRecipient> recipients, NotificationContent content) {
        // Implementation for push notifications
    }
    
    private void sendSMSNotifications(Notification notification, Set<NotificationRecipient> recipients, NotificationContent content) {
        // Implementation for SMS notifications
    }
    
    private void sendVoiceNotifications(Notification notification, Set<NotificationRecipient> recipients, NotificationContent content) {
        // Implementation for voice notifications
    }
    
    private void recordDelivery(Notification notification, Notification.NotificationChannel channel, String userId, String messageId, NotificationDelivery.DeliveryStatus status) {
        // Implementation to record delivery status
    }
    
    private List<Alert> getAlertsForEscalation() {
        // Implementation to get alerts requiring escalation
        return List.of(); // Simplified for now
    }
    
    private void escalateAlert(Alert alert) {
        // Implementation for alert escalation
    }
    
    private void updateEmailDeliveryStatus() {
        // Implementation to update email delivery status
    }
    
    private void updatePushNotificationStatus() {
        // Implementation to update push notification status
    }
    
    private void processReadReceipts() {
        // Implementation to process read receipts
    }
    
    // Supporting classes (simplified for compilation)
    public static class NotificationRequest {
        private String title;
        private Set<String> recipients;
        private Set<Notification.NotificationChannel> channels;
        
        // Getters and setters
        public String getTitle() { return title; }
        public Set<String> getRecipients() { return recipients; }
        public Set<Notification.NotificationChannel> getChannels() { return channels; }
    }
    
    public static class NotificationRecipient {
        private String userId;
        private String emailAddress;
        private String slackChannel;
        private boolean emailEnabled = true;
        private boolean slackEnabled = true;
        
        // Getters and setters
        public String getUserId() { return userId; }
        public String getEmailAddress() { return emailAddress; }
        public String getSlackChannel() { return slackChannel; }
        public boolean isEmailEnabled() { return emailEnabled; }
        public boolean isSlackEnabled() { return slackEnabled; }
    }
    
    public static class NotificationContent {
        private String emailSubject;
        private String emailHtmlContent;
        private String emailTextContent;
        private String emailTemplateId;
        private String slackText;
        private Object slackAttachments;
        private Object slackBlocks;
        private Object templateData;
        
        // Getters and setters
        public String getEmailSubject() { return emailSubject; }
        public String getEmailHtmlContent() { return emailHtmlContent; }
        public String getEmailTextContent() { return emailTextContent; }
        public String getEmailTemplateId() { return emailTemplateId; }
        public String getSlackText() { return slackText; }
        public Object getSlackAttachments() { return slackAttachments; }
        public Object getSlackBlocks() { return slackBlocks; }
        public Object getTemplateData() { return templateData; }
    }
    
    public static class EmailMessage {
        private String to;
        private String subject;
        private String htmlContent;
        private String textContent;
        private String templateId;
        private Object templateData;
        
        public static EmailMessageBuilder builder() {
            return new EmailMessageBuilder();
        }
        
        // Getters and setters
        public String getTo() { return to; }
        public String getSubject() { return subject; }
        public String getHtmlContent() { return htmlContent; }
        public String getTextContent() { return textContent; }
        public String getTemplateId() { return templateId; }
        public Object getTemplateData() { return templateData; }
        
        public static class EmailMessageBuilder {
            private EmailMessage message = new EmailMessage();
            
            public EmailMessageBuilder to(String to) { message.to = to; return this; }
            public EmailMessageBuilder subject(String subject) { message.subject = subject; return this; }
            public EmailMessageBuilder htmlContent(String htmlContent) { message.htmlContent = htmlContent; return this; }
            public EmailMessageBuilder textContent(String textContent) { message.textContent = textContent; return this; }
            public EmailMessageBuilder templateId(String templateId) { message.templateId = templateId; return this; }
            public EmailMessageBuilder templateData(Object templateData) { message.templateData = templateData; return this; }
            public EmailMessage build() { return message; }
        }
    }
    
    public static class SlackMessage {
        private String channel;
        private String text;
        private Object attachments;
        private Object blocks;
        
        public static SlackMessageBuilder builder() {
            return new SlackMessageBuilder();
        }
        
        // Getters and setters
        public String getChannel() { return channel; }
        public String getText() { return text; }
        public Object getAttachments() { return attachments; }
        public Object getBlocks() { return blocks; }
        
        public static class SlackMessageBuilder {
            private SlackMessage message = new SlackMessage();
            
            public SlackMessageBuilder channel(String channel) { message.channel = channel; return this; }
            public SlackMessageBuilder text(String text) { message.text = text; return this; }
            public SlackMessageBuilder attachments(Object attachments) { message.attachments = attachments; return this; }
            public SlackMessageBuilder blocks(Object blocks) { message.blocks = blocks; return this; }
            public SlackMessage build() { return message; }
        }
    }
    
    // Exception classes
    public static class NotificationException extends RuntimeException {
        public NotificationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    public static class AlertNotFoundException extends RuntimeException {
        public AlertNotFoundException(String message) {
            super(message);
        }
    }
}
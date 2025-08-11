package com.supplychainrisk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Enhanced notification service for multi-channel alerts
 * Supports email, Slack, Teams, and WebSocket notifications
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Value("${notification.email.enabled:true}")
    private boolean emailEnabled;

    @Value("${notification.slack.enabled:false}")
    private boolean slackEnabled;

    @Value("${notification.slack.webhook.url:}")
    private String slackWebhookUrl;

    @Value("${notification.teams.enabled:false}")
    private boolean teamsEnabled;

    @Value("${notification.teams.webhook.url:}")
    private String teamsWebhookUrl;

    @Value("${notification.email.from:noreply@supplychainrisk.com}")
    private String fromEmail;

    private final JavaMailSender mailSender;
    private final RestTemplate restTemplate;
    private final RealTimeUpdateService realTimeUpdateService;

    public NotificationService(JavaMailSender mailSender, 
                              RealTimeUpdateService realTimeUpdateService) {
        this.mailSender = mailSender;
        this.realTimeUpdateService = realTimeUpdateService;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Send multi-channel notification for critical alerts
     */
    public void sendCriticalAlert(String title, String message, String recipientEmail, 
                                 Map<String, Object> alertData) {
        logger.info("Sending critical alert: {}", title);

        // Send via all enabled channels asynchronously
        CompletableFuture.allOf(
            CompletableFuture.runAsync(() -> sendEmailNotification(recipientEmail, title, message, alertData)),
            CompletableFuture.runAsync(() -> sendSlackNotification(title, message, alertData, "critical")),
            CompletableFuture.runAsync(() -> sendTeamsNotification(title, message, alertData, "critical")),
            CompletableFuture.runAsync(() -> sendWebSocketNotification(title, message, alertData, "critical"))
        );
    }

    /**
     * Send delay prediction alert
     */
    public void sendDelayAlert(String shipmentId, double predictedDelayHours, 
                              String riskLevel, List<String> recommendations, 
                              String recipientEmail) {
        String title = String.format("Shipment Delay Prediction - %s Risk", riskLevel);
        String message = String.format(
            "Shipment %s is predicted to be delayed by %.1f hours. Risk level: %s",
            shipmentId, predictedDelayHours, riskLevel
        );

        Map<String, Object> alertData = new HashMap<>();
        alertData.put("type", "delay_prediction");
        alertData.put("shipmentId", shipmentId);
        alertData.put("predictedDelayHours", predictedDelayHours);
        alertData.put("riskLevel", riskLevel);
        alertData.put("recommendations", recommendations);
        alertData.put("timestamp", LocalDateTime.now());

        if ("CRITICAL".equals(riskLevel) || "HIGH".equals(riskLevel)) {
            sendCriticalAlert(title, message, recipientEmail, alertData);
        } else {
            sendRoutineAlert(title, message, recipientEmail, alertData);
        }
    }

    /**
     * Send supplier anomaly alert
     */
    public void sendSupplierAnomalyAlert(String supplierName, String anomalyType, 
                                        String severity, String description, 
                                        List<String> recommendations, String recipientEmail) {
        String title = String.format("Supplier Anomaly Detected - %s (%s)", supplierName, severity.toUpperCase());
        String message = String.format(
            "Anomaly detected for supplier %s: %s. Type: %s",
            supplierName, description, anomalyType
        );

        Map<String, Object> alertData = new HashMap<>();
        alertData.put("type", "supplier_anomaly");
        alertData.put("supplierName", supplierName);
        alertData.put("anomalyType", anomalyType);
        alertData.put("severity", severity);
        alertData.put("description", description);
        alertData.put("recommendations", recommendations);
        alertData.put("timestamp", LocalDateTime.now());

        if ("high".equals(severity)) {
            sendCriticalAlert(title, message, recipientEmail, alertData);
        } else {
            sendRoutineAlert(title, message, recipientEmail, alertData);
        }
    }

    /**
     * Send routine alert (lower priority)
     */
    public void sendRoutineAlert(String title, String message, String recipientEmail, 
                                Map<String, Object> alertData) {
        logger.info("Sending routine alert: {}", title);

        CompletableFuture.allOf(
            CompletableFuture.runAsync(() -> sendEmailNotification(recipientEmail, title, message, alertData)),
            CompletableFuture.runAsync(() -> sendWebSocketNotification(title, message, alertData, "medium"))
        );
    }

    /**
     * Send email notification
     */
    private void sendEmailNotification(String recipientEmail, String title, String message, 
                                      Map<String, Object> alertData) {
        if (!emailEnabled || recipientEmail == null || recipientEmail.isEmpty()) {
            logger.debug("Email notification skipped - disabled or no recipient");
            return;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(recipientEmail);
            helper.setSubject(title);
            helper.setText(createEmailBody(title, message, alertData), true);

            mailSender.send(mimeMessage);
            logger.info("Email notification sent to: {}", recipientEmail);

        } catch (Exception e) {
            logger.error("Failed to send email notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Send Slack notification
     */
    private void sendSlackNotification(String title, String message, Map<String, Object> alertData, 
                                      String priority) {
        if (!slackEnabled || slackWebhookUrl.isEmpty()) {
            logger.debug("Slack notification skipped - disabled or no webhook URL");
            return;
        }

        try {
            Map<String, Object> slackMessage = createSlackMessage(title, message, alertData, priority);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(slackMessage, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(slackWebhookUrl, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Slack notification sent successfully");
            } else {
                logger.warn("Slack notification failed with status: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            logger.error("Failed to send Slack notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Send Teams notification
     */
    private void sendTeamsNotification(String title, String message, Map<String, Object> alertData, 
                                      String priority) {
        if (!teamsEnabled || teamsWebhookUrl.isEmpty()) {
            logger.debug("Teams notification skipped - disabled or no webhook URL");
            return;
        }

        try {
            Map<String, Object> teamsMessage = createTeamsMessage(title, message, alertData, priority);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(teamsMessage, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(teamsWebhookUrl, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Teams notification sent successfully");
            } else {
                logger.warn("Teams notification failed with status: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            logger.error("Failed to send Teams notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Send WebSocket notification
     */
    private void sendWebSocketNotification(String title, String message, Map<String, Object> alertData, 
                                          String priority) {
        try {
            Map<String, Object> wsMessage = new HashMap<>();
            wsMessage.put("type", "notification");
            wsMessage.put("title", title);
            wsMessage.put("message", message);
            wsMessage.put("priority", priority);
            wsMessage.put("data", alertData);
            wsMessage.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            realTimeUpdateService.broadcastToTopic("/topic/notifications", wsMessage);
            logger.debug("WebSocket notification sent: {}", title);

        } catch (Exception e) {
            logger.error("Failed to send WebSocket notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Create HTML email body
     */
    private String createEmailBody(String title, String message, Map<String, Object> alertData) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; color: #333; }");
        html.append(".header { background-color: #f8f9fa; padding: 20px; border-left: 4px solid #007bff; }");
        html.append(".content { padding: 20px; }");
        html.append(".alert-critical { border-left-color: #dc3545; }");
        html.append(".alert-high { border-left-color: #fd7e14; }");
        html.append(".alert-medium { border-left-color: #ffc107; }");
        html.append(".footer { background-color: #f8f9fa; padding: 10px; font-size: 12px; color: #666; }");
        html.append("</style></head><body>");

        String priority = (String) alertData.getOrDefault("priority", "medium");
        html.append("<div class='header alert-").append(priority).append("'>");
        html.append("<h2>").append(title).append("</h2>");
        html.append("</div>");

        html.append("<div class='content'>");
        html.append("<p>").append(message).append("</p>");

        // Add specific alert details
        if (alertData.containsKey("recommendations")) {
            html.append("<h3>Recommendations:</h3><ul>");
            @SuppressWarnings("unchecked")
            List<String> recommendations = (List<String>) alertData.get("recommendations");
            for (String recommendation : recommendations) {
                html.append("<li>").append(recommendation).append("</li>");
            }
            html.append("</ul>");
        }

        html.append("<p><strong>Time:</strong> ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("</p>");
        html.append("</div>");

        html.append("<div class='footer'>");
        html.append("<p>Smart Supply Chain Risk Intelligence System</p>");
        html.append("<p>This is an automated notification. Please do not reply to this email.</p>");
        html.append("</div>");

        html.append("</body></html>");
        return html.toString();
    }

    /**
     * Create Slack message format
     */
    private Map<String, Object> createSlackMessage(String title, String message, 
                                                  Map<String, Object> alertData, String priority) {
        Map<String, Object> slackMessage = new HashMap<>();
        
        String color = getColorForPriority(priority);
        String emoji = getEmojiForPriority(priority);
        
        slackMessage.put("text", emoji + " " + title);
        
        Map<String, Object> attachment = new HashMap<>();
        attachment.put("color", color);
        attachment.put("text", message);
        attachment.put("footer", "Smart Supply Chain Risk Intelligence");
        attachment.put("ts", System.currentTimeMillis() / 1000);
        
        // Add fields for specific alert types
        if (alertData.containsKey("shipmentId")) {
            Map<String, Object> field1 = new HashMap<>();
            field1.put("title", "Shipment ID");
            field1.put("value", alertData.get("shipmentId"));
            field1.put("short", true);
            
            attachment.put("fields", List.of(field1));
        }
        
        slackMessage.put("attachments", List.of(attachment));
        
        return slackMessage;
    }

    /**
     * Create Teams message format
     */
    private Map<String, Object> createTeamsMessage(String title, String message, 
                                                  Map<String, Object> alertData, String priority) {
        Map<String, Object> teamsMessage = new HashMap<>();
        teamsMessage.put("@type", "MessageCard");
        teamsMessage.put("@context", "http://schema.org/extensions");
        teamsMessage.put("summary", title);
        teamsMessage.put("themeColor", getColorForPriority(priority));
        
        Map<String, Object> section = new HashMap<>();
        section.put("activityTitle", title);
        section.put("activitySubtitle", "Smart Supply Chain Risk Intelligence");
        section.put("text", message);
        
        teamsMessage.put("sections", List.of(section));
        
        return teamsMessage;
    }

    private String getColorForPriority(String priority) {
        switch (priority.toLowerCase()) {
            case "critical": return "#dc3545";
            case "high": return "#fd7e14";
            case "medium": return "#ffc107";
            default: return "#28a745";
        }
    }

    private String getEmojiForPriority(String priority) {
        switch (priority.toLowerCase()) {
            case "critical": return "🚨";
            case "high": return "⚠️";
            case "medium": return "📢";
            default: return "ℹ️";
        }
    }
}
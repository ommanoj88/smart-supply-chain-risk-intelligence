package com.supplychainrisk.service;

import com.supplychainrisk.entity.Alert;
import com.supplychainrisk.service.AdvancedNotificationService.NotificationContent;
import com.supplychainrisk.service.AdvancedNotificationService.SlackMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class SlackService {
    
    private static final Logger logger = LoggerFactory.getLogger(SlackService.class);
    
    @Value("${notification.slack.webhook.url:}")
    private String webhookUrl;
    
    @Value("${notification.slack.bot.token:}")
    private String botToken;
    
    @Value("${notification.slack.enabled:false}")
    private boolean slackEnabled;
    
    public String sendMessage(SlackMessage message) {
        try {
            if (!slackEnabled) {
                logger.info("Slack notifications disabled - would send message to channel: {}", message.getChannel());
                return "slack-disabled-" + System.currentTimeMillis();
            }
            
            // Build Slack payload
            SlackPayload payload = SlackPayload.builder()
                .channel(message.getChannel())
                .text(message.getText())
                .attachments(message.getAttachments())
                .blocks(message.getBlocks())
                .build();
            
            // Send via webhook or bot API
            if (message.isInteractive()) {
                return sendViaSlackAPI(payload);
            } else {
                return sendViaWebhook(payload);
            }
            
        } catch (Exception e) {
            logger.error("Failed to send Slack message", e);
            throw new SlackException("Slack message sending failed", e);
        }
    }
    
    public SlackMessage createAlertMessage(Alert alert, NotificationContent content) {
        return SlackMessage.builder()
            .channel(getAlertChannel(alert.getSeverity()))
            .text(content.getSlackText())
            .attachments(Arrays.asList(
                SlackAttachment.builder()
                    .color(getAlertColor(alert.getSeverity()))
                    .title(alert.getTitle())
                    .text(alert.getDescription())
                    .fields(Arrays.asList(
                        SlackField.builder().title("Severity").value(alert.getSeverity().toString()).shortField(true).build(),
                        SlackField.builder().title("Risk Score").value(alert.getRiskScore() != null ? alert.getRiskScore().toString() : "N/A").shortField(true).build(),
                        SlackField.builder().title("Source").value(alert.getSourceSystem()).shortField(true).build(),
                        SlackField.builder().title("Detected").value(formatDateTime(alert.getDetectedAt())).shortField(true).build()
                    ))
                    .actions(Arrays.asList(
                        SlackAction.builder()
                            .type("button")
                            .text("Acknowledge")
                            .value("acknowledge_" + alert.getId())
                            .style("primary")
                            .build(),
                        SlackAction.builder()
                            .type("button")
                            .text("View Details")
                            .url(generateAlertUrl(alert.getId()))
                            .build()
                    ))
                    .build()
            ))
            .build();
    }
    
    private String sendViaWebhook(SlackPayload payload) {
        try {
            // Webhook implementation would go here
            // For now, simulate sending
            logger.info("Simulating Slack webhook send to channel: {} with text: {}", 
                       payload.getChannel(), payload.getText());
            return "webhook-message-id-" + System.currentTimeMillis();
            
        } catch (Exception e) {
            logger.error("Slack webhook sending failed", e);
            throw new SlackException("Slack webhook sending failed", e);
        }
    }
    
    private String sendViaSlackAPI(SlackPayload payload) {
        try {
            // Slack API implementation would go here
            // For now, simulate sending
            logger.info("Simulating Slack API send to channel: {} with text: {}", 
                       payload.getChannel(), payload.getText());
            return "api-message-id-" + System.currentTimeMillis();
            
        } catch (Exception e) {
            logger.error("Slack API sending failed", e);
            throw new SlackException("Slack API sending failed", e);
        }
    }
    
    private String getAlertChannel(Alert.AlertSeverity severity) {
        return switch (severity) {
            case CRITICAL -> "#alerts-critical";
            case HIGH -> "#alerts-high";
            case MEDIUM -> "#alerts-medium";
            case LOW -> "#alerts-low";
        };
    }
    
    private String getAlertColor(Alert.AlertSeverity severity) {
        return switch (severity) {
            case CRITICAL -> "#FF0000"; // Red
            case HIGH -> "#FF8000"; // Orange
            case MEDIUM -> "#FFFF00"; // Yellow
            case LOW -> "#008000"; // Green
        };
    }
    
    private String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    private String generateAlertUrl(Long alertId) {
        return "http://localhost:3000/alerts/" + alertId;
    }
    
    // Supporting classes
    public static class SlackPayload {
        private String channel;
        private String text;
        private Object attachments;
        private Object blocks;
        
        public static SlackPayloadBuilder builder() {
            return new SlackPayloadBuilder();
        }
        
        // Getters and setters
        public String getChannel() { return channel; }
        public String getText() { return text; }
        public Object getAttachments() { return attachments; }
        public Object getBlocks() { return blocks; }
        
        public static class SlackPayloadBuilder {
            private SlackPayload payload = new SlackPayload();
            
            public SlackPayloadBuilder channel(String channel) { payload.channel = channel; return this; }
            public SlackPayloadBuilder text(String text) { payload.text = text; return this; }
            public SlackPayloadBuilder attachments(Object attachments) { payload.attachments = attachments; return this; }
            public SlackPayloadBuilder blocks(Object blocks) { payload.blocks = blocks; return this; }
            public SlackPayload build() { return payload; }
        }
    }
    
    public static class SlackAttachment {
        private String color;
        private String title;
        private String text;
        private List<SlackField> fields;
        private List<SlackAction> actions;
        
        public static SlackAttachmentBuilder builder() {
            return new SlackAttachmentBuilder();
        }
        
        // Getters and setters
        public String getColor() { return color; }
        public String getTitle() { return title; }
        public String getText() { return text; }
        public List<SlackField> getFields() { return fields; }
        public List<SlackAction> getActions() { return actions; }
        
        public static class SlackAttachmentBuilder {
            private SlackAttachment attachment = new SlackAttachment();
            
            public SlackAttachmentBuilder color(String color) { attachment.color = color; return this; }
            public SlackAttachmentBuilder title(String title) { attachment.title = title; return this; }
            public SlackAttachmentBuilder text(String text) { attachment.text = text; return this; }
            public SlackAttachmentBuilder fields(List<SlackField> fields) { attachment.fields = fields; return this; }
            public SlackAttachmentBuilder actions(List<SlackAction> actions) { attachment.actions = actions; return this; }
            public SlackAttachment build() { return attachment; }
        }
    }
    
    public static class SlackField {
        private String title;
        private String value;
        private boolean shortField;
        
        public static SlackFieldBuilder builder() {
            return new SlackFieldBuilder();
        }
        
        // Getters and setters
        public String getTitle() { return title; }
        public String getValue() { return value; }
        public boolean isShortField() { return shortField; }
        
        public static class SlackFieldBuilder {
            private SlackField field = new SlackField();
            
            public SlackFieldBuilder title(String title) { field.title = title; return this; }
            public SlackFieldBuilder value(String value) { field.value = value; return this; }
            public SlackFieldBuilder shortField(boolean shortField) { field.shortField = shortField; return this; }
            public SlackField build() { return field; }
        }
    }
    
    public static class SlackAction {
        private String type;
        private String text;
        private String value;
        private String style;
        private String url;
        
        public static SlackActionBuilder builder() {
            return new SlackActionBuilder();
        }
        
        // Getters and setters
        public String getType() { return type; }
        public String getText() { return text; }
        public String getValue() { return value; }
        public String getStyle() { return style; }
        public String getUrl() { return url; }
        
        public static class SlackActionBuilder {
            private SlackAction action = new SlackAction();
            
            public SlackActionBuilder type(String type) { action.type = type; return this; }
            public SlackActionBuilder text(String text) { action.text = text; return this; }
            public SlackActionBuilder value(String value) { action.value = value; return this; }
            public SlackActionBuilder style(String style) { action.style = style; return this; }
            public SlackActionBuilder url(String url) { action.url = url; return this; }
            public SlackAction build() { return action; }
        }
    }
    
    // Exception class
    public static class SlackException extends RuntimeException {
        public SlackException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
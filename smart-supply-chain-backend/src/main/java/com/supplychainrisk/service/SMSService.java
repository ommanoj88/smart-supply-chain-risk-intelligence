package com.supplychainrisk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SMSService {
    
    private static final Logger logger = LoggerFactory.getLogger(SMSService.class);
    
    @Value("${notification.sms.enabled:false}")
    private boolean smsEnabled;
    
    @Value("${notification.sms.twilio.account.sid:}")
    private String twilioAccountSid;
    
    @Value("${notification.sms.twilio.auth.token:}")
    private String twilioAuthToken;
    
    @Value("${notification.sms.from.number:}")
    private String fromNumber;
    
    public String sendSMS(SMSMessage message) {
        try {
            if (!smsEnabled) {
                logger.info("SMS notifications disabled - would send to: {}", message.getToNumber());
                return "sms-disabled-" + System.currentTimeMillis();
            }
            
            // Validate message
            validateSMSMessage(message);
            
            // Send via Twilio
            return sendViaTwilio(message);
            
        } catch (Exception e) {
            logger.error("Failed to send SMS", e);
            throw new SMSException("SMS sending failed", e);
        }
    }
    
    private String sendViaTwilio(SMSMessage message) {
        try {
            // Twilio implementation would go here
            // For now, simulate sending
            logger.info("Simulating Twilio SMS send to: {} with message: {}", 
                       message.getToNumber(), message.getBody());
            return "twilio-message-id-" + System.currentTimeMillis();
            
        } catch (Exception e) {
            logger.error("Twilio SMS sending failed", e);
            throw new SMSException("Twilio SMS sending failed", e);
        }
    }
    
    private void validateSMSMessage(SMSMessage message) {
        if (message.getToNumber() == null || message.getToNumber().trim().isEmpty()) {
            throw new SMSException("Recipient phone number is required");
        }
        
        if (message.getBody() == null || message.getBody().trim().isEmpty()) {
            throw new SMSException("SMS message body is required");
        }
        
        if (message.getBody().length() > 1600) {
            throw new SMSException("SMS message body is too long (max 1600 characters)");
        }
    }
    
    // Supporting classes
    public static class SMSMessage {
        private String toNumber;
        private String body;
        private String fromNumber;
        private String mediaUrl;
        
        // Getters and setters
        public String getToNumber() { return toNumber; }
        public void setToNumber(String toNumber) { this.toNumber = toNumber; }
        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }
        public String getFromNumber() { return fromNumber; }
        public void setFromNumber(String fromNumber) { this.fromNumber = fromNumber; }
        public String getMediaUrl() { return mediaUrl; }
        public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    }
    
    // Exception class
    public static class SMSException extends RuntimeException {
        public SMSException(String message) {
            super(message);
        }
        
        public SMSException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
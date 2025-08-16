package com.supplychainrisk.service;

import com.supplychainrisk.service.AdvancedNotificationService.EmailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    @Value("${app.name:Smart Supply Chain Risk Intelligence}")
    private String appName;
    
    @Autowired(required = false)
    private NotificationTemplateService templateService;
    
    @Value("${notification.email.provider:smtp}")
    private String emailProvider;
    
    @Value("${notification.email.from.address:noreply@supplychainrisk.com}")
    private String fromAddress;
    
    @Value("${notification.email.from.name:Supply Chain Risk Intelligence}")
    private String fromName;
    
    // Enhanced method for advanced notifications
    public String sendEmail(EmailMessage message) {
        try {
            // Process template if specified
            if (message.getTemplateId() != null && templateService != null) {
                processTemplate(message);
            }
            
            // Validate email content
            validateEmailMessage(message);
            
            // Send via configured provider
            switch (emailProvider.toLowerCase()) {
                case "ses":
                    return sendViaSES(message);
                case "sendgrid":
                    return sendViaSendGrid(message);
                case "smtp":
                    return sendViaSMTP(message);
                default:
                    return sendViaSMTP(message); // Default to SMTP
            }
            
        } catch (Exception e) {
            logger.error("Failed to send email: {}", message.getSubject(), e);
            throw new EmailException("Email sending failed", e);
        }
    }
    
    private String sendViaSES(EmailMessage message) {
        try {
            // AWS SES implementation would go here
            // For now, simulate sending
            logger.info("Simulating SES email send to: {} with subject: {}", message.getTo(), message.getSubject());
            return "ses-message-id-" + System.currentTimeMillis();
            
        } catch (Exception e) {
            logger.error("SES email sending failed", e);
            throw new EmailException("SES email sending failed", e);
        }
    }
    
    private String sendViaSendGrid(EmailMessage message) {
        try {
            // SendGrid implementation would go here
            // For now, simulate sending
            logger.info("Simulating SendGrid email send to: {} with subject: {}", message.getTo(), message.getSubject());
            return "sendgrid-message-id-" + System.currentTimeMillis();
            
        } catch (Exception e) {
            logger.error("SendGrid email sending failed", e);
            throw new EmailException("SendGrid email sending failed", e);
        }
    }
    
    private String sendViaSMTP(EmailMessage message) {
        try {
            // SMTP implementation would go here
            // For now, simulate sending
            logger.info("Simulating SMTP email send to: {} with subject: {}", message.getTo(), message.getSubject());
            return "smtp-message-id-" + System.currentTimeMillis();
            
        } catch (Exception e) {
            logger.error("SMTP email sending failed", e);
            throw new EmailException("SMTP email sending failed", e);
        }
    }
    
    private void processTemplate(EmailMessage message) {
        if (templateService != null) {
            // Template processing would be implemented here
            logger.info("Processing email template: {}", message.getTemplateId());
        }
    }
    
    private void validateEmailMessage(EmailMessage message) {
        if (message.getTo() == null || message.getTo().trim().isEmpty()) {
            throw new EmailException("Recipient email address is required");
        }
        
        if (message.getSubject() == null || message.getSubject().trim().isEmpty()) {
            throw new EmailException("Email subject is required");
        }
        
        if ((message.getHtmlContent() == null || message.getHtmlContent().trim().isEmpty()) &&
            (message.getTextContent() == null || message.getTextContent().trim().isEmpty())) {
            throw new EmailException("Email content (HTML or text) is required");
        }
    }
    
    private String formatEmailAddress(String email, String name) {
        if (name != null && !name.trim().isEmpty()) {
            return String.format("%s <%s>", name, email);
        }
        return email;
    }
    
    public void sendPasswordResetEmail(String email, String name, String resetToken) {
        try {
            // In a real implementation, you would use a proper email service like SendGrid, AWS SES, etc.
            // For now, we'll just log the email content
            
            String resetLink = frontendUrl + "/auth/reset-password?token=" + resetToken;
            
            String emailBody = String.format("""
                Dear %s,
                
                You requested a password reset for your %s account.
                
                Please click the link below to reset your password:
                %s
                
                This link will expire in 1 hour.
                
                If you did not request this password reset, please ignore this email.
                
                Best regards,
                %s Team
                """, name, appName, resetLink, appName);
            
            logger.info("Password reset email would be sent to: {}", email);
            logger.debug("Email body: {}", emailBody);
            
            // TODO: Implement actual email sending logic here
            // Example with JavaMailSender:
            // MimeMessage message = javaMailSender.createMimeMessage();
            // MimeMessageHelper helper = new MimeMessageHelper(message, true);
            // helper.setTo(email);
            // helper.setSubject("Password Reset - " + appName);
            // helper.setText(emailBody, false);
            // javaMailSender.send(message);
            
        } catch (Exception e) {
            logger.error("Failed to send password reset email to {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
    
    public void sendPasswordResetConfirmationEmail(String email, String name) {
        try {
            String emailBody = String.format("""
                Dear %s,
                
                Your password has been successfully reset for your %s account.
                
                If you did not perform this action, please contact our support team immediately.
                
                Best regards,
                %s Team
                """, name, appName, appName);
            
            logger.info("Password reset confirmation email would be sent to: {}", email);
            logger.debug("Email body: {}", emailBody);
            
            // TODO: Implement actual email sending logic here
            
        } catch (Exception e) {
            logger.error("Failed to send password reset confirmation email to {}: {}", email, e.getMessage());
            // Don't throw exception for confirmation emails as they're not critical
        }
    }
    
    public void sendEmailVerificationEmail(String email, String name, String verificationToken) {
        try {
            String verificationLink = frontendUrl + "/auth/verify-email?token=" + verificationToken;
            
            String emailBody = String.format("""
                Dear %s,
                
                Welcome to %s!
                
                Please click the link below to verify your email address:
                %s
                
                This link will expire in 24 hours.
                
                Best regards,
                %s Team
                """, name, appName, verificationLink, appName);
            
            logger.info("Email verification email would be sent to: {}", email);
            logger.debug("Email body: {}", emailBody);
            
            // TODO: Implement actual email sending logic here
            
        } catch (Exception e) {
            logger.error("Failed to send email verification email to {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to send email verification email", e);
        }
    }
    
    // Exception class
    public static class EmailException extends RuntimeException {
        public EmailException(String message) {
            super(message);
        }
        
        public EmailException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
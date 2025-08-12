package com.supplychainrisk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    @Value("${app.name:Smart Supply Chain Risk Intelligence}")
    private String appName;
    
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
}
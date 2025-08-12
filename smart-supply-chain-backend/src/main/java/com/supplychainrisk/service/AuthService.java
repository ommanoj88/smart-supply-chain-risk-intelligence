package com.supplychainrisk.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.supplychainrisk.entity.User;
import com.supplychainrisk.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 30;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private RateLimitService rateLimitService;

    public FirebaseToken verifyToken(String idToken) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }

    public User getOrCreateUser(FirebaseToken decodedToken) {
        String firebaseUid = decodedToken.getUid();
        String email = decodedToken.getEmail();
        String name = decodedToken.getName();

        Optional<User> existingUser = userRepository.findByFirebaseUid(firebaseUid);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Update email and name if they have changed
            boolean updated = false;
            if (email != null && !email.equals(user.getEmail())) {
                user.setEmail(email);
                updated = true;
            }
            if (name != null && !name.equals(user.getName())) {
                user.setName(name);
                updated = true;
            }
            if (updated) {
                return userRepository.save(user);
            }
            return user;
        } else {
            // Create new user with default VIEWER role
            User newUser = new User(firebaseUid, email, name, User.Role.VIEWER);
            return userRepository.save(newUser);
        }
    }

    public Optional<User> getUserByFirebaseUid(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid);
    }
    
    // Enhanced login attempt tracking
    @Transactional
    public boolean recordFailedLoginAttempt(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return false; // Don't reveal if email exists
        }
        
        User user = userOpt.get();
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES));
            logger.warn("User account locked due to too many failed attempts: {}", email);
        }
        
        userRepository.save(user);
        return attempts >= MAX_FAILED_ATTEMPTS;
    }
    
    @Transactional
    public void resetFailedLoginAttempts(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            userRepository.save(user);
        }
    }
    
    public boolean isAccountLocked(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        return user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now());
    }
    
    // Enhanced Password Reset Methods
    
    @Transactional
    public boolean initiatePasswordReset(String email, String clientId) {
        try {
            // Additional rate limiting for password reset
            if (!rateLimitService.isAllowed(clientId, RateLimitService.RateLimitType.CRITICAL)) {
                logger.warn("Password reset rate limit exceeded for client: {}", clientId);
                return false;
            }
            
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                logger.warn("Password reset requested for non-existent email: {}", email);
                // Always return true to prevent email enumeration
                return true;
            }
            
            User user = userOpt.get();
            
            // Check if user is active
            if (!user.getIsActive()) {
                logger.warn("Password reset requested for inactive user: {}", email);
                return true; // Don't reveal account status
            }
            
            String resetToken = generateSecureToken();
            LocalDateTime expiresAt = LocalDateTime.now().plusHours(1); // Token expires in 1 hour
            
            user.setPasswordResetToken(resetToken);
            user.setPasswordResetTokenExpires(expiresAt);
            userRepository.save(user);
            
            // Send email with reset link
            emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), resetToken);
            
            logger.info("Password reset token generated and email sent for user: {}", user.getEmail());
            return true;
            
        } catch (Exception e) {
            logger.error("Error initiating password reset for email {}: {}", email, e.getMessage());
            return false;
        }
    }
    
    public boolean validatePasswordResetToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return false;
            }
            
            Optional<User> userOpt = userRepository.findByPasswordResetToken(token.trim());
            if (userOpt.isEmpty()) {
                return false;
            }
            
            User user = userOpt.get();
            LocalDateTime now = LocalDateTime.now();
            
            // Check if user is active
            if (!user.getIsActive()) {
                return false;
            }
            
            // Check if token has expired
            if (user.getPasswordResetTokenExpires() == null || 
                user.getPasswordResetTokenExpires().isBefore(now)) {
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            logger.error("Error validating password reset token: {}", e.getMessage());
            return false;
        }
    }
    
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        try {
            if (!validatePasswordResetToken(token)) {
                return false;
            }
            
            Optional<User> userOpt = userRepository.findByPasswordResetToken(token.trim());
            if (userOpt.isEmpty()) {
                return false;
            }
            
            User user = userOpt.get();
            
            // Update password with strong encoding
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            user.setPasswordResetToken(null);
            user.setPasswordResetTokenExpires(null);
            user.setUpdatedAt(LocalDateTime.now());
            
            // Reset failed login attempts
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            
            userRepository.save(user);
            
            // Send confirmation email
            emailService.sendPasswordResetConfirmationEmail(user.getEmail(), user.getName());
            
            logger.info("Password successfully reset for user: {}", user.getEmail());
            return true;
            
        } catch (Exception e) {
            logger.error("Error resetting password: {}", e.getMessage());
            return false;
        }
    }
    
    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return UUID.randomUUID().toString().replace("-", "") + 
               java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
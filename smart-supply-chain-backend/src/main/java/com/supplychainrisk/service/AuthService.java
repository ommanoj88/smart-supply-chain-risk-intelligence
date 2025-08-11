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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

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
            if (!email.equals(user.getEmail())) {
                user.setEmail(email);
                updated = true;
            }
            if (!name.equals(user.getName())) {
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
    
    // Password Reset Methods
    
    public String generatePasswordResetToken(String email) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                logger.warn("Password reset requested for non-existent email: {}", email);
                return null; // Don't reveal if email exists
            }
            
            User user = userOpt.get();
            String resetToken = UUID.randomUUID().toString();
            LocalDateTime expiresAt = LocalDateTime.now().plusHours(1); // Token expires in 1 hour
            
            user.setPasswordResetToken(resetToken);
            user.setPasswordResetTokenExpires(expiresAt);
            userRepository.save(user);
            
            logger.info("Password reset token generated for user: {}", user.getEmail());
            return resetToken;
            
        } catch (Exception e) {
            logger.error("Error generating password reset token for email {}: {}", email, e.getMessage());
            throw e;
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
            
            // Update password
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            user.setPasswordResetToken(null);
            user.setPasswordResetTokenExpires(null);
            user.setUpdatedAt(LocalDateTime.now());
            
            userRepository.save(user);
            
            logger.info("Password successfully reset for user: {}", user.getEmail());
            return true;
            
        } catch (Exception e) {
            logger.error("Error resetting password: {}", e.getMessage());
            return false;
        }
    }
}
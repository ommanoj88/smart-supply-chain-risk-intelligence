package com.supply.chain.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.supply.chain.model.User;
import com.supply.chain.model.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FirebaseAuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(FirebaseAuthService.class);
    
    public FirebaseToken verifyToken(String idToken) throws FirebaseAuthException {
        try {
            return FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (FirebaseAuthException e) {
            logger.error("Failed to verify Firebase token: {}", e.getMessage());
            throw e;
        }
    }
    
    public User createUserFromToken(FirebaseToken decodedToken) {
        String uid = decodedToken.getUid();
        String email = decodedToken.getEmail();
        String name = decodedToken.getName();
        String picture = (String) decodedToken.getClaims().get("picture");
        
        User user = new User(uid, email, name != null ? name : email);
        user.setPhotoURL(picture);
        
        // Default role is VIEWER, can be updated by admin
        user.setRole(UserRole.VIEWER);
        
        logger.info("Created user from Firebase token: {} ({})", user.getDisplayName(), user.getEmail());
        
        return user;
    }
    
    public String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
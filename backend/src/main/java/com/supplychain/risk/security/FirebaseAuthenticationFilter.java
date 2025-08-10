package com.supplychain.risk.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.supplychain.risk.entity.User;
import com.supplychain.risk.enums.UserRole;
import com.supplychain.risk.exception.CustomAuthenticationException;
import com.supplychain.risk.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

/**
 * Firebase Authentication Filter for JWT token verification.
 * 
 * This filter intercepts HTTP requests and validates Firebase ID tokens
 * to authenticate users in the Smart Supply Chain Risk Intelligence platform.
 * It creates Spring Security authentication context based on Firebase token verification.
 * 
 * @author Supply Chain Risk Intelligence Team
 * @version 1.0
 * @since 2024-01-01
 */
@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(FirebaseAuthenticationFilter.class);
    
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    @Autowired(required = false)
    private FirebaseAuth firebaseAuth;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Performs the actual filtering logic for Firebase authentication.
     * 
     * This method:
     * 1. Extracts the Firebase ID token from the Authorization header
     * 2. Verifies the token with Firebase Auth service
     * 3. Retrieves or creates the user in the local database
     * 4. Sets up Spring Security authentication context
     * 
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String token = extractTokenFromRequest(request);
            
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Check if Firebase is available
                if (firebaseAuth == null) {
                    logger.debug("Firebase not configured - skipping authentication for development");
                } else {
                    authenticateWithFirebaseToken(token, request);
                }
            }
            
        } catch (Exception e) {
            logger.error("Firebase authentication failed", e);
            // Clear any existing authentication
            SecurityContextHolder.clearContext();
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Extracts the Firebase ID token from the Authorization header.
     * 
     * @param request the HTTP request
     * @return the Firebase token if present, null otherwise
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        
        return null;
    }
    
    /**
     * Authenticates the user with the provided Firebase token.
     * 
     * @param token the Firebase ID token
     * @param request the HTTP request for setting authentication details
     * @throws CustomAuthenticationException if authentication fails
     */
    private void authenticateWithFirebaseToken(String token, HttpServletRequest request) {
        try {
            // Verify the token with Firebase Auth service
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token);
            String firebaseUid = decodedToken.getUid();
            String email = decodedToken.getEmail();
            String name = decodedToken.getName();
            
            logger.debug("Firebase token verified for user: {} ({})", email, firebaseUid);
            
            // Get or create user in local database
            User user = getOrCreateUser(firebaseUid, email, name);
            
            // Check if user account is active
            if (!user.isActive()) {
                logger.warn("Inactive user attempted to authenticate: {}", email);
                throw CustomAuthenticationException.accountInactive(email);
            }
            
            // Create Spring Security authentication
            createAuthentication(user, request);
            
            logger.debug("Authentication successful for user: {} with role: {}", email, user.getRole());
            
        } catch (FirebaseAuthException e) {
            logger.error("Firebase token verification failed", e);
            throw CustomAuthenticationException.invalidToken(e);
        } catch (Exception e) {
            logger.error("Unexpected error during Firebase authentication", e);
            throw CustomAuthenticationException.firebaseError(e);
        }
    }
    
    /**
     * Retrieves an existing user or creates a new one based on Firebase authentication.
     * 
     * @param firebaseUid the Firebase UID
     * @param email the user's email
     * @param name the user's name
     * @return the User entity
     */
    private User getOrCreateUser(String firebaseUid, String email, String name) {
        Optional<User> existingUser = userRepository.findByFirebaseUid(firebaseUid);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            
            // Update user information if it has changed
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
                user = userRepository.save(user);
                logger.debug("Updated existing user: {}", email);
            }
            
            return user;
        } else {
            // Create new user with default VIEWER role
            User newUser = new User();
            newUser.setFirebaseUid(firebaseUid);
            newUser.setEmail(email != null ? email : "unknown@example.com");
            newUser.setName(name != null ? name : "Unknown User");
            newUser.setRole(UserRole.VIEWER);
            newUser.setActive(true);
            
            newUser = userRepository.save(newUser);
            logger.info("Created new user: {} with role: {}", email, UserRole.VIEWER);
            
            return newUser;
        }
    }
    
    /**
     * Creates Spring Security authentication context for the authenticated user.
     * 
     * @param user the authenticated user
     * @param request the HTTP request
     */
    private void createAuthentication(User user, HttpServletRequest request) {
        // Create authorities based on user role
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getAuthority());
        
        // Create authentication token
        UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                    user.getEmail(), 
                    null, 
                    Collections.singletonList(authority)
                );
        
        // Set authentication details
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        
        // Set the authentication in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    
    /**
     * Determines if the filter should be applied to the given request.
     * 
     * @param request the HTTP request
     * @return true if the filter should be applied, false otherwise
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip authentication for public endpoints
        return path.startsWith("/api/public/") ||
               path.equals("/api/health") ||
               path.equals("/api/auth/verify") ||
               path.startsWith("/h2-console") ||
               path.startsWith("/actuator/") ||
               request.getMethod().equals("OPTIONS");
    }
}
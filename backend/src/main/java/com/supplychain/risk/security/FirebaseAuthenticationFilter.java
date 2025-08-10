package com.supplychain.risk.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.supplychain.risk.entity.User;
import com.supplychain.risk.enums.UserRole;
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

@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(FirebaseAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    @Autowired(required = false)
    private FirebaseAuth firebaseAuth;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String token = extractTokenFromRequest(request);
            
            if (StringUtils.hasText(token) && firebaseAuth != null) {
                FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token);
                String firebaseUid = decodedToken.getUid();
                String email = decodedToken.getEmail();
                String name = decodedToken.getName();
                
                logger.debug("Verified Firebase token for user: {} ({})", name, email);
                
                // Find or create user in database
                User user = findOrCreateUser(firebaseUid, email, name);
                
                if (user != null && user.getIsActive()) {
                    // Create authentication object
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                        );
                    
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    logger.debug("User {} authenticated successfully with role {}", user.getEmail(), user.getRole());
                } else {
                    logger.warn("User {} is inactive or not found", email);
                }
            } else if (firebaseAuth == null) {
                logger.debug("Firebase authentication is disabled in development mode");
                // In development mode without Firebase, skip authentication
            }
        } catch (FirebaseAuthException e) {
            logger.error("Firebase authentication failed: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Authentication error: {}", e.getMessage(), e);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(BEARER_PREFIX)) {
            return authorizationHeader.substring(BEARER_PREFIX.length());
        }
        
        return null;
    }
    
    private User findOrCreateUser(String firebaseUid, String email, String name) {
        try {
            // Try to find existing user by Firebase UID
            Optional<User> existingUser = userRepository.findByFirebaseUid(firebaseUid);
            
            if (existingUser.isPresent()) {
                return existingUser.get();
            }
            
            // Try to find by email (for migration scenarios)
            Optional<User> userByEmail = userRepository.findByEmail(email);
            if (userByEmail.isPresent()) {
                User user = userByEmail.get();
                user.setFirebaseUid(firebaseUid);
                return userRepository.save(user);
            }
            
            // Create new user with default VIEWER role
            User newUser = new User();
            newUser.setFirebaseUid(firebaseUid);
            newUser.setEmail(email);
            newUser.setName(name != null ? name : email);
            newUser.setRole(UserRole.VIEWER); // Default role
            newUser.setIsActive(true);
            
            logger.info("Creating new user: {} ({})", newUser.getName(), newUser.getEmail());
            return userRepository.save(newUser);
            
        } catch (Exception e) {
            logger.error("Error finding or creating user: {}", e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip authentication for public endpoints
        return path.startsWith("/api/auth/") ||
               path.startsWith("/h2-console") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/api-docs") ||
               path.startsWith("/actuator/health") ||
               path.equals("/api/");
    }
}
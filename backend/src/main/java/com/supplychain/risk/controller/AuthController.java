package com.supplychain.risk.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.supplychain.risk.dto.ApiResponse;
import com.supplychain.risk.dto.LoginRequest;
import com.supplychain.risk.dto.UserDto;
import com.supplychain.risk.exception.CustomAuthenticationException;
import com.supplychain.risk.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for authentication operations.
 * 
 * This controller handles Firebase authentication, token verification,
 * and user profile operations for the Smart Supply Chain Risk Intelligence platform.
 * 
 * @author Supply Chain Risk Intelligence Team
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "https://smart-supply-chain.netlify.app"})
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired(required = false)
    private FirebaseAuth firebaseAuth;
    
    @Autowired
    private UserService userService;
    
    /**
     * Authenticates a user with Firebase token and returns user profile.
     * 
     * @param loginRequest the login request containing Firebase token
     * @return ResponseEntity with authentication result and user profile
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Authentication request received");
        
        try {
            // Check if Firebase is configured
            if (firebaseAuth == null) {
                return ResponseEntity.status(503)
                        .body(ApiResponse.internalError("Firebase authentication is not configured"));
            }
            
            // Verify Firebase token
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(loginRequest.getFirebaseToken());
            String firebaseUid = decodedToken.getUid();
            String email = decodedToken.getEmail();
            String name = decodedToken.getName();
            
            logger.debug("Firebase token verified for user: {} ({})", email, firebaseUid);
            
            // Get or create user in local database
            UserDto user = userService.getOrCreateUserFromFirebase(firebaseUid, email, name);
            
            // Check if user account is active
            if (!user.isActive()) {
                logger.warn("Inactive user attempted to login: {}", email);
                return ResponseEntity.status(403)
                        .body(ApiResponse.forbidden("User account is inactive"));
            }
            
            // Prepare response data
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("user", user);
            responseData.put("authenticated", true);
            responseData.put("role", user.getRole());
            responseData.put("permissions", getRolePermissions(user.getRole().name()));
            
            logger.info("User authenticated successfully: {} with role: {}", email, user.getRole());
            
            return ResponseEntity.ok(ApiResponse.success("Authentication successful", responseData));
            
        } catch (FirebaseAuthException e) {
            logger.error("Firebase token verification failed", e);
            return ResponseEntity.status(401)
                    .body(ApiResponse.unauthorized("Invalid or expired Firebase token"));
                    
        } catch (Exception e) {
            logger.error("Unexpected error during authentication", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.internalError("Authentication service error"));
        }
    }
    
    /**
     * Verifies the current authentication status and returns user profile.
     * 
     * @return ResponseEntity with current user information
     */
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyAuthentication() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.unauthorized("No active authentication"));
            }
            
            String email = authentication.getName();
            UserDto user = userService.getUserByEmail(email);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("user", user);
            responseData.put("authenticated", true);
            responseData.put("role", user.getRole());
            responseData.put("permissions", getRolePermissions(user.getRole().name()));
            
            logger.debug("Authentication verified for user: {}", email);
            
            return ResponseEntity.ok(ApiResponse.success("Authentication verified", responseData));
            
        } catch (Exception e) {
            logger.error("Error verifying authentication", e);
            return ResponseEntity.status(401)
                    .body(ApiResponse.unauthorized("Authentication verification failed"));
        }
    }
    
    /**
     * Logs out the current user by clearing the security context.
     * Note: Firebase token invalidation should be handled on the client side.
     * 
     * @return ResponseEntity with logout confirmation
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null) {
                String email = authentication.getName();
                logger.info("User logged out: {}", email);
            }
            
            // Clear the security context
            SecurityContextHolder.clearContext();
            
            return ResponseEntity.ok(ApiResponse.success("Logout successful"));
            
        } catch (Exception e) {
            logger.error("Error during logout", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.internalError("Logout failed"));
        }
    }
    
    /**
     * Refreshes user authentication by verifying token and updating user profile.
     * 
     * @param loginRequest the refresh request containing Firebase token
     * @return ResponseEntity with refreshed user information
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, Object>>> refreshAuthentication(@Valid @RequestBody LoginRequest loginRequest) {
        logger.debug("Authentication refresh request received");
        
        try {
            // Check if Firebase is configured
            if (firebaseAuth == null) {
                return ResponseEntity.status(503)
                        .body(ApiResponse.internalError("Firebase authentication is not configured"));
            }
            
            // Verify Firebase token
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(loginRequest.getFirebaseToken());
            String firebaseUid = decodedToken.getUid();
            String email = decodedToken.getEmail();
            String name = decodedToken.getName();
            
            // Get updated user information
            UserDto user = userService.getOrCreateUserFromFirebase(firebaseUid, email, name);
            
            // Check if user account is still active
            if (!user.isActive()) {
                logger.warn("Inactive user attempted to refresh authentication: {}", email);
                return ResponseEntity.status(403)
                        .body(ApiResponse.forbidden("User account is inactive"));
            }
            
            // Prepare response data
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("user", user);
            responseData.put("authenticated", true);
            responseData.put("role", user.getRole());
            responseData.put("permissions", getRolePermissions(user.getRole().name()));
            
            logger.debug("Authentication refreshed for user: {}", email);
            
            return ResponseEntity.ok(ApiResponse.success("Authentication refreshed", responseData));
            
        } catch (FirebaseAuthException e) {
            logger.error("Firebase token verification failed during refresh", e);
            return ResponseEntity.status(401)
                    .body(ApiResponse.unauthorized("Invalid or expired Firebase token"));
                    
        } catch (Exception e) {
            logger.error("Unexpected error during authentication refresh", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.internalError("Authentication refresh failed"));
        }
    }
    
    /**
     * Gets the current user's profile information.
     * 
     * @return ResponseEntity with user profile
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto>> getProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.unauthorized("Authentication required"));
            }
            
            String email = authentication.getName();
            UserDto user = userService.getUserByEmail(email);
            
            logger.debug("Profile retrieved for user: {}", email);
            
            return ResponseEntity.ok(ApiResponse.success("Profile retrieved", user));
            
        } catch (Exception e) {
            logger.error("Error retrieving user profile", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.internalError("Failed to retrieve profile"));
        }
    }
    
    /**
     * Updates the current user's profile information.
     * 
     * @param userDto the updated user profile data
     * @return ResponseEntity with updated profile
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(@Valid @RequestBody UserDto userDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.unauthorized("Authentication required"));
            }
            
            String email = authentication.getName();
            UserDto currentUser = userService.getUserByEmail(email);
            
            // Users can only update their own profile and limited fields
            UserDto updatedUser = userService.updateUser(currentUser.getId(), userDto);
            
            logger.info("Profile updated for user: {}", email);
            
            return ResponseEntity.ok(ApiResponse.success("Profile updated", updatedUser));
            
        } catch (Exception e) {
            logger.error("Error updating user profile", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.internalError("Failed to update profile"));
        }
    }
    
    /**
     * Gets role-based permissions for the frontend.
     * 
     * @param role the user role
     * @return map of permissions
     */
    private Map<String, Boolean> getRolePermissions(String role) {
        Map<String, Boolean> permissions = new HashMap<>();
        
        switch (role) {
            case "ADMIN":
                permissions.put("canManageUsers", true);
                permissions.put("canManageSupplyChain", true);
                permissions.put("canViewAnalytics", true);
                permissions.put("canManageRiskAssessments", true);
                permissions.put("canExportData", true);
                permissions.put("canManageSystem", true);
                break;
                
            case "SUPPLY_MANAGER":
                permissions.put("canManageUsers", false);
                permissions.put("canManageSupplyChain", true);
                permissions.put("canViewAnalytics", true);
                permissions.put("canManageRiskAssessments", true);
                permissions.put("canExportData", true);
                permissions.put("canManageSystem", false);
                break;
                
            case "VIEWER":
                permissions.put("canManageUsers", false);
                permissions.put("canManageSupplyChain", false);
                permissions.put("canViewAnalytics", true);
                permissions.put("canManageRiskAssessments", false);
                permissions.put("canExportData", false);
                permissions.put("canManageSystem", false);
                break;
                
            default:
                // Default to minimal permissions
                permissions.put("canManageUsers", false);
                permissions.put("canManageSupplyChain", false);
                permissions.put("canViewAnalytics", false);
                permissions.put("canManageRiskAssessments", false);
                permissions.put("canExportData", false);
                permissions.put("canManageSystem", false);
        }
        
        return permissions;
    }
}
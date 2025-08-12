package com.supplychainrisk.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.supplychainrisk.dto.AuthResponse;
import com.supplychainrisk.dto.LoginRequest;
import com.supplychainrisk.dto.RegisterRequest;
import com.supplychainrisk.dto.ForgotPasswordRequest;
import com.supplychainrisk.dto.ResetPasswordRequest;
import com.supplychainrisk.entity.User;
import com.supplychainrisk.security.JwtUtil;
import com.supplychainrisk.service.AuthService;
import com.supplychainrisk.service.UserService;
import com.supplychainrisk.service.UserSessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserSessionService userSessionService;

    // JWT Authentication Endpoints
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            String identifier = loginRequest.getIdentifier();
            String password = loginRequest.getPassword();
            
            // Check if account is locked
            if (userService.isAccountLocked(identifier)) {
                return ResponseEntity.status(HttpStatus.LOCKED)
                    .body(Map.of("error", "Account is temporarily locked due to multiple failed login attempts"));
            }
            
            // Find user by username or email
            Optional<User> userOptional = userService.findByUsernameOrEmail(identifier);
            if (userOptional.isEmpty()) {
                userService.incrementFailedLoginAttempts(identifier);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
            }
            
            User user = userOptional.get();
            
            // Validate password
            if (!userService.validatePassword(password, user.getPasswordHash())) {
                userService.incrementFailedLoginAttempts(identifier);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
            }
            
            // Check if user is active
            if (!user.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Account is deactivated"));
            }
            
            // Generate JWT token
            String token = jwtUtil.generateToken(user.getUsername() != null ? user.getUsername() : user.getEmail(), 
                                               user.getRole().name(), user.getId());
            
            // Create session
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            userSessionService.createSession(user, token, ipAddress, userAgent);
            
            // Update last login
            userService.updateLastLogin(user.getId());
            
            // Return response
            AuthResponse.UserDTO userDTO = new AuthResponse.UserDTO(user);
            AuthResponse response = new AuthResponse(token, userDTO);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error"));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest, HttpServletRequest request) {
        try {
            // Only allow self-registration as VIEWER, admins can create other roles
            User.Role role = registerRequest.getRole();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            // If not authenticated or not admin, default to VIEWER
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                role = User.Role.VIEWER;
            } else {
                User currentUser = (User) authentication.getPrincipal();
                if (currentUser.getRole() != User.Role.ADMIN) {
                    role = User.Role.VIEWER;
                }
            }
            
            // Create user
            User user = userService.createUser(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                registerRequest.getFirstName(),
                registerRequest.getLastName(),
                role
            );
            
            // Generate JWT token
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name(), user.getId());
            
            // Create session
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            userSessionService.createSession(user, token, ipAddress, userAgent);
            
            // Return response
            AuthResponse.UserDTO userDTO = new AuthResponse.UserDTO(user);
            AuthResponse response = new AuthResponse(token, userDTO);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Registration error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error"));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                userSessionService.revokeSession(token);
            }
            
            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
        } catch (Exception e) {
            logger.error("Logout error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error"));
        }
    }
    
    // Firebase Authentication Endpoints (existing)

    @PostMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestBody Map<String, String> tokenRequest) {
        try {
            String idToken = tokenRequest.get("idToken");
            
            if (idToken == null || idToken.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "ID token is required"));
            }

            FirebaseToken decodedToken = authService.verifyToken(idToken);
            User user = authService.getOrCreateUser(decodedToken);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", Map.of(
                "id", user.getId(),
                "firebaseUid", user.getFirebaseUid(),
                "email", user.getEmail(),
                "name", user.getName(),
                "role", user.getRole().name(),
                "createdAt", user.getCreatedAt(),
                "updatedAt", user.getUpdatedAt()
            ));

            return ResponseEntity.ok(response);

        } catch (FirebaseAuthException e) {
            logger.error("Firebase token verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid authentication token"));
        } catch (Exception e) {
            logger.error("Token verification error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
            }

            User user = (User) authentication.getPrincipal();

            Map<String, Object> response = Map.of(
                "id", user.getId(),
                "firebaseUid", user.getFirebaseUid(),
                "email", user.getEmail(),
                "name", user.getName(),
                "role", user.getRole().name(),
                "createdAt", user.getCreatedAt(),
                "updatedAt", user.getUpdatedAt()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error retrieving current user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error"));
        }
    }

    @PutMapping("/user/role")
    public ResponseEntity<?> updateUserRole(@RequestBody Map<String, String> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
            }

            User currentUser = (User) authentication.getPrincipal();
            
            // Only admins can update user roles
            if (currentUser.getRole() != User.Role.ADMIN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Insufficient permissions"));
            }

            String firebaseUid = request.get("firebaseUid");
            String roleString = request.get("role");

            if (firebaseUid == null || roleString == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Firebase UID and role are required"));
            }

            User.Role role = User.Role.valueOf(roleString.toUpperCase());
            User updatedUser = userService.updateUserRole(firebaseUid, role);

            Map<String, Object> response = Map.of(
                "success", true,
                "user", Map.of(
                    "id", updatedUser.getId(),
                    "firebaseUid", updatedUser.getFirebaseUid(),
                    "email", updatedUser.getEmail(),
                    "name", updatedUser.getName(),
                    "role", updatedUser.getRole().name(),
                    "updatedAt", updatedUser.getUpdatedAt()
                )
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid role specified"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating user role: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error"));
        }
    }
    
    // Password Reset Endpoints
    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            // Generate password reset token and send email
            boolean success = authService.initiatePasswordReset(request.getEmail());
            
            if (success) {
                return ResponseEntity.ok(Map.of(
                    "message", "Password reset email sent successfully",
                    "email", request.getEmail()
                ));
            } else {
                // Don't reveal if email exists or not for security
                return ResponseEntity.ok(Map.of(
                    "message", "If the email exists, a password reset link will be sent",
                    "email", request.getEmail()
                ));
            }
            
        } catch (Exception e) {
            logger.error("Forgot password error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error"));
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            boolean success = authService.resetPassword(request.getToken(), request.getNewPassword());
            
            if (success) {
                return ResponseEntity.ok(Map.of(
                    "message", "Password has been reset successfully"
                ));
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid or expired reset token"));
            }
            
        } catch (Exception e) {
            logger.error("Reset password error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error"));
        }
    }
    
    @PostMapping("/validate-reset-token")
    public ResponseEntity<?> validateResetToken(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Token is required"));
            }
            
            boolean isValid = authService.validatePasswordResetToken(token);
            
            return ResponseEntity.ok(Map.of(
                "valid", isValid,
                "message", isValid ? "Token is valid" : "Token is invalid or expired"
            ));
            
        } catch (Exception e) {
            logger.error("Validate reset token error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error"));
        }
    }
    
    // Helper method to extract token from request
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    // Helper method to get client IP address
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        }
        return xForwardedForHeader.split(",")[0];
    }
}
package com.supplychainrisk.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.supplychainrisk.entity.User;
import com.supplychainrisk.service.AuthService;
import com.supplychainrisk.service.UserService;
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

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

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
}
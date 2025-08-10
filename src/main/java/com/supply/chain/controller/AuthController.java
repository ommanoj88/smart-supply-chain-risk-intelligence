package com.supply.chain.controller;

import com.google.firebase.auth.FirebaseToken;
import com.supply.chain.model.User;
import com.supply.chain.model.UserRole;
import com.supply.chain.service.FirebaseAuthService;
import com.supply.chain.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private FirebaseAuthService firebaseAuthService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String idToken = firebaseAuthService.extractTokenFromHeader(authorizationHeader);
            if (idToken == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid authorization header"));
            }
            
            FirebaseToken decodedToken = firebaseAuthService.verifyToken(idToken);
            User user = firebaseAuthService.createUserFromToken(decodedToken);
            
            // Create or update user in database
            User savedUser = userService.createOrUpdateUser(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", savedUser);
            response.put("message", "Login successful");
            
            logger.info("User logged in successfully: {}", savedUser.getEmail());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String idToken = firebaseAuthService.extractTokenFromHeader(authorizationHeader);
            if (idToken == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid authorization header"));
            }
            
            FirebaseToken decodedToken = firebaseAuthService.verifyToken(idToken);
            Optional<User> userOpt = userService.getUserById(decodedToken.getUid());
            
            if (userOpt.isPresent()) {
                return ResponseEntity.ok(userOpt.get());
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("Failed to get profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String idToken = firebaseAuthService.extractTokenFromHeader(authorizationHeader);
            if (idToken == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid authorization header"));
            }
            
            FirebaseToken decodedToken = firebaseAuthService.verifyToken(idToken);
            Optional<User> currentUserOpt = userService.getUserById(decodedToken.getUid());
            
            if (currentUserOpt.isPresent() && currentUserOpt.get().getRole() == UserRole.ADMIN) {
                List<User> users = userService.getAllUsers();
                return ResponseEntity.ok(users);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin access required"));
            }
            
        } catch (Exception e) {
            logger.error("Failed to get users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication failed: " + e.getMessage()));
        }
    }
    
    @PutMapping("/users/{uid}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable String uid,
            @RequestBody Map<String, String> roleUpdate,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String idToken = firebaseAuthService.extractTokenFromHeader(authorizationHeader);
            if (idToken == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid authorization header"));
            }
            
            FirebaseToken decodedToken = firebaseAuthService.verifyToken(idToken);
            Optional<User> currentUserOpt = userService.getUserById(decodedToken.getUid());
            
            if (currentUserOpt.isPresent() && currentUserOpt.get().getRole() == UserRole.ADMIN) {
                String roleStr = roleUpdate.get("role");
                UserRole role = UserRole.valueOf(roleStr.toUpperCase());
                
                User updatedUser = userService.updateUserRole(uid, role);
                return ResponseEntity.ok(updatedUser);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin access required"));
            }
            
        } catch (Exception e) {
            logger.error("Failed to update user role: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to update role: " + e.getMessage()));
        }
    }
}
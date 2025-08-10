package com.supplychain.risk.controller;

import com.supplychain.risk.dto.ApiResponse;
import com.supplychain.risk.dto.LoginRequest;
import com.supplychain.risk.dto.UserDto;
import com.supplychain.risk.entity.User;
import com.supplychain.risk.enums.UserRole;
import com.supplychain.risk.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and user management endpoints")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    @Operation(summary = "Authenticate user with Firebase ID token")
    public ResponseEntity<ApiResponse<UserDto>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // The Firebase authentication is handled by the filter
            // If we reach here, the user is authenticated
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                
                // Update user info if needed
                user.setName(loginRequest.getName());
                if (loginRequest.getProfilePictureUrl() != null) {
                    user.setProfilePictureUrl(loginRequest.getProfilePictureUrl());
                }
                
                UserDto userDto = userService.updateUser(user.getId(), convertToDto(user));
                
                return ResponseEntity.ok(ApiResponse.success("Login successful", userDto));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Authentication failed", "Invalid credentials"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Login failed", e.getMessage()));
        }
    }
    
    @GetMapping("/me")
    @Operation(summary = "Get current user information")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                UserDto userDto = userService.getUserById(user.getId());
                
                return ResponseEntity.ok(ApiResponse.success(userDto));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("User not authenticated"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get user info", e.getMessage()));
        }
    }
    
    @PostMapping("/logout")
    @Operation(summary = "Logout user")
    public ResponseEntity<ApiResponse<String>> logout() {
        try {
            SecurityContextHolder.clearContext();
            return ResponseEntity.ok(ApiResponse.success("Logout successful", "User logged out successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Logout failed", e.getMessage()));
        }
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check endpoint")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Service is healthy", "Authentication service is running"));
    }
    
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
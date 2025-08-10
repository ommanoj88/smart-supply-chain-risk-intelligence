package com.smartsupplychain.riskintelligence.controller;

import com.smartsupplychain.riskintelligence.dto.ApiResponse;
import com.smartsupplychain.riskintelligence.dto.UserDto;
import com.smartsupplychain.riskintelligence.security.FirebaseUserPrincipal;
import com.smartsupplychain.riskintelligence.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<UserDto>> verifyToken(Authentication authentication) {
        try {
            String firebaseUid = (String) authentication.getPrincipal();
            FirebaseUserPrincipal userPrincipal = (FirebaseUserPrincipal) authentication.getDetails();
            
            // Create or update user in database
            UserDto user = userService.createOrUpdateUserFromFirebase(
                    firebaseUid, 
                    userPrincipal.getEmail(), 
                    userPrincipal.getName()
            );
            
            return ResponseEntity.ok(ApiResponse.success("Authentication successful", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Authentication failed: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(Authentication authentication) {
        try {
            String firebaseUid = (String) authentication.getPrincipal();
            UserDto user = userService.getUserByFirebaseUid(firebaseUid);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get user info: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("API is healthy"));
    }
}
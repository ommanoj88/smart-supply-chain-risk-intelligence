package com.supplychain.risk.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for login requests.
 * 
 * This DTO represents the payload for Firebase authentication requests,
 * containing the Firebase ID token that needs to be verified.
 * 
 * @author Supply Chain Risk Intelligence Team
 * @version 1.0
 * @since 2024-01-01
 */
public class LoginRequest {
    
    /**
     * Firebase ID token obtained from client-side Firebase authentication.
     * This token will be verified against Firebase Auth service.
     */
    @NotBlank(message = "Firebase token is required")
    private String firebaseToken;
    
    /**
     * Optional field to indicate if this is a refresh token request.
     */
    private boolean refreshToken = false;
    
    /**
     * Default constructor.
     */
    public LoginRequest() {}
    
    /**
     * Constructor with Firebase token.
     * 
     * @param firebaseToken the Firebase ID token
     */
    public LoginRequest(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
    
    /**
     * Constructor with Firebase token and refresh flag.
     * 
     * @param firebaseToken the Firebase ID token
     * @param refreshToken indicates if this is a refresh request
     */
    public LoginRequest(String firebaseToken, boolean refreshToken) {
        this.firebaseToken = firebaseToken;
        this.refreshToken = refreshToken;
    }
    
    // Getters and Setters
    
    public String getFirebaseToken() {
        return firebaseToken;
    }
    
    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
    
    public boolean isRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(boolean refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    @Override
    public String toString() {
        return "LoginRequest{" +
                "firebaseToken='" + (firebaseToken != null ? "[REDACTED]" : "null") + '\'' +
                ", refreshToken=" + refreshToken +
                '}';
    }
}
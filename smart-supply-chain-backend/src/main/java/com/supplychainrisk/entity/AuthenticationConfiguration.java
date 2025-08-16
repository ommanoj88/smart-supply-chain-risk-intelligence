package com.supplychainrisk.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class AuthenticationConfiguration {
    
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type")
    private AuthenticationType authType;
    
    @Column(name = "username", length = 100)
    private String username;
    
    @Column(name = "password_hash", columnDefinition = "TEXT")
    private String passwordHash;
    
    @Column(name = "api_key", columnDefinition = "TEXT")
    private String apiKey;
    
    @Column(name = "oauth_client_id", length = 255)
    private String oauthClientId;
    
    @Column(name = "oauth_client_secret", columnDefinition = "TEXT")
    private String oauthClientSecret;
    
    @Column(name = "oauth_token_url", length = 255)
    private String oauthTokenUrl;
    
    @Column(name = "oauth_scope", length = 255)
    private String oauthScope;
    
    @Column(name = "certificate_path", length = 255)
    private String certificatePath;
    
    @Column(name = "custom_auth_headers", columnDefinition = "TEXT")
    private String customAuthHeaders;
    
    public enum AuthenticationType {
        BASIC, API_KEY, OAUTH2, CERTIFICATE, CUSTOM
    }
    
    // Default constructor
    public AuthenticationConfiguration() {}
    
    // Getters and Setters
    public AuthenticationType getAuthType() {
        return authType;
    }
    
    public void setAuthType(AuthenticationType authType) {
        this.authType = authType;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public String getOauthClientId() {
        return oauthClientId;
    }
    
    public void setOauthClientId(String oauthClientId) {
        this.oauthClientId = oauthClientId;
    }
    
    public String getOauthClientSecret() {
        return oauthClientSecret;
    }
    
    public void setOauthClientSecret(String oauthClientSecret) {
        this.oauthClientSecret = oauthClientSecret;
    }
    
    public String getOauthTokenUrl() {
        return oauthTokenUrl;
    }
    
    public void setOauthTokenUrl(String oauthTokenUrl) {
        this.oauthTokenUrl = oauthTokenUrl;
    }
    
    public String getOauthScope() {
        return oauthScope;
    }
    
    public void setOauthScope(String oauthScope) {
        this.oauthScope = oauthScope;
    }
    
    public String getCertificatePath() {
        return certificatePath;
    }
    
    public void setCertificatePath(String certificatePath) {
        this.certificatePath = certificatePath;
    }
    
    public String getCustomAuthHeaders() {
        return customAuthHeaders;
    }
    
    public void setCustomAuthHeaders(String customAuthHeaders) {
        this.customAuthHeaders = customAuthHeaders;
    }
}
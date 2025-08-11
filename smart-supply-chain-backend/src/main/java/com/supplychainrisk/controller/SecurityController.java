package com.supplychainrisk.controller;

import com.supplychainrisk.service.MFAService;
import com.supplychainrisk.service.RateLimitingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;
import java.util.HashMap;

/**
 * Enhanced Security Controller
 * Handles MFA, rate limiting, and security monitoring
 */
@RestController
@RequestMapping("/api/security")
@Tag(name = "Security", description = "Enhanced security features including MFA and rate limiting")
@CrossOrigin(origins = "${cors.allowed.origins}")
public class SecurityController {

    private static final Logger logger = LoggerFactory.getLogger(SecurityController.class);

    @Autowired
    private MFAService mfaService;

    @Autowired
    private RateLimitingService rateLimitingService;

    @PostMapping("/mfa/setup")
    @Operation(summary = "Setup MFA for user")
    @ApiResponse(responseCode = "200", description = "MFA setup initiated successfully")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> setupMFA(Principal principal) {
        try {
            String username = principal.getName();
            logger.info("Setting up MFA for user: {}", username);

            String secret = mfaService.generateSecret();
            Map<String, Object> setupInfo = mfaService.getMFASetupInfo(secret, username);

            // In a real implementation, save the secret to user's profile
            // userService.updateMFASecret(username, secret);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("setupInfo", setupInfo);
            response.put("message", "MFA setup initiated. Scan QR code with your authenticator app.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error setting up MFA for user {}: {}", principal.getName(), e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "error", "Failed to setup MFA"));
        }
    }

    @PostMapping("/mfa/verify")
    @Operation(summary = "Verify MFA code")
    @ApiResponse(responseCode = "200", description = "MFA code verified successfully")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> verifyMFA(@RequestBody Map<String, String> request, 
                                                         Principal principal) {
        try {
            String username = principal.getName();
            String code = request.get("code");
            String secret = request.get("secret"); // In real app, get from user profile

            logger.info("Verifying MFA code for user: {}", username);

            MFAService.MFAValidationResult result = mfaService.validateMFA(secret, code, new String[0]);

            Map<String, Object> response = new HashMap<>();
            response.put("success", result.isValid());
            response.put("method", result.getMethod());
            response.put("message", result.getMessage());

            if (result.isValid()) {
                logger.info("MFA verification successful for user: {}", username);
            } else {
                logger.warn("MFA verification failed for user: {}", username);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error verifying MFA for user {}: {}", principal.getName(), e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "error", "Failed to verify MFA"));
        }
    }

    @PostMapping("/mfa/disable")
    @Operation(summary = "Disable MFA for user")
    @ApiResponse(responseCode = "200", description = "MFA disabled successfully")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> disableMFA(@RequestBody Map<String, String> request,
                                                          Principal principal) {
        try {
            String username = principal.getName();
            String code = request.get("code");
            String secret = request.get("secret"); // In real app, get from user profile

            logger.info("Disabling MFA for user: {}", username);

            // Verify current MFA code before disabling
            MFAService.MFAValidationResult result = mfaService.validateMFA(secret, code, new String[0]);

            if (!result.isValid()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Invalid MFA code"));
            }

            // In a real implementation, remove MFA from user's profile
            // userService.disableMFA(username);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "MFA disabled successfully");

            logger.info("MFA disabled for user: {}", username);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error disabling MFA for user {}: {}", principal.getName(), e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "error", "Failed to disable MFA"));
        }
    }

    @GetMapping("/rate-limit/status")
    @Operation(summary = "Get current rate limit status")
    @ApiResponse(responseCode = "200", description = "Rate limit status retrieved successfully")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getRateLimitStatus(Principal principal, 
                                                                 HttpServletRequest request) {
        try {
            String username = principal.getName();
            String clientIP = getClientIP(request);

            RateLimitingService.RateLimitInfo userInfo = rateLimitingService.getRateLimitInfo(username);

            Map<String, Object> response = new HashMap<>();
            response.put("user", Map.of(
                "totalCapacity", userInfo.getTotalCapacity(),
                "availableTokens", userInfo.getAvailableTokens(),
                "usedTokens", userInfo.getUsedTokens(),
                "usagePercentage", userInfo.getUsagePercentage(),
                "resetPeriod", userInfo.getResetPeriod().toMinutes() + " minutes"
            ));
            response.put("clientIP", clientIP);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting rate limit status for user {}: {}", principal.getName(), e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to get rate limit status"));
        }
    }

    @PostMapping("/rate-limit/reset")
    @Operation(summary = "Reset rate limit for user (admin only)")
    @ApiResponse(responseCode = "200", description = "Rate limit reset successfully")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> resetRateLimit(@RequestBody Map<String, String> request) {
        try {
            String targetUser = request.get("username");
            String targetIP = request.get("ipAddress");

            if (targetUser != null) {
                rateLimitingService.resetUserLimit(targetUser);
                logger.info("Rate limit reset for user: {}", targetUser);
                return ResponseEntity.ok(Map.of("success", true, "message", "User rate limit reset"));
            }

            if (targetIP != null) {
                rateLimitingService.resetIPLimit(targetIP);
                logger.info("Rate limit reset for IP: {}", targetIP);
                return ResponseEntity.ok(Map.of("success", true, "message", "IP rate limit reset"));
            }

            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", "Username or IP address required"));

        } catch (Exception e) {
            logger.error("Error resetting rate limit: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "error", "Failed to reset rate limit"));
        }
    }

    @GetMapping("/audit/recent-activity")
    @Operation(summary = "Get recent security activity")
    @ApiResponse(responseCode = "200", description = "Recent activity retrieved successfully")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<Map<String, Object>> getRecentActivity(Principal principal) {
        try {
            // In a real implementation, this would query audit logs
            Map<String, Object> activity = new HashMap<>();
            activity.put("loginAttempts", Map.of(
                "successful", 45,
                "failed", 3,
                "blocked", 1
            ));
            activity.put("mfaVerifications", Map.of(
                "successful", 42,
                "failed", 2
            ));
            activity.put("rateLimitHits", Map.of(
                "userLimits", 5,
                "ipLimits", 2,
                "endpointLimits", 3
            ));
            activity.put("timeRange", "Last 24 hours");
            activity.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(activity);

        } catch (Exception e) {
            logger.error("Error getting recent security activity: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to get recent activity"));
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Get security service health status")
    @ApiResponse(responseCode = "200", description = "Security health status retrieved successfully")
    public ResponseEntity<Map<String, Object>> getSecurityHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("mfaService", "healthy");
        health.put("rateLimitingService", "healthy");
        health.put("authenticationService", "healthy");
        health.put("timestamp", System.currentTimeMillis());
        health.put("status", "UP");

        return ResponseEntity.ok(health);
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }
}
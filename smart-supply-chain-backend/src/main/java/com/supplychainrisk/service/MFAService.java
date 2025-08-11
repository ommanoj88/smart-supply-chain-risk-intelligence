package com.supplychainrisk.service;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Multi-Factor Authentication Service
 * Handles TOTP-based MFA for enhanced security
 */
@Service
public class MFAService {

    private static final Logger logger = LoggerFactory.getLogger(MFAService.class);
    private static final String ISSUER = "Smart Supply Chain RI";

    private final SecretGenerator secretGenerator;
    private final QrGenerator qrGenerator;
    private final CodeGenerator codeGenerator;
    private final CodeVerifier codeVerifier;
    private final TimeProvider timeProvider;

    public MFAService() {
        this.secretGenerator = new DefaultSecretGenerator();
        this.qrGenerator = new ZxingPngQrGenerator();
        this.codeGenerator = new DefaultCodeGenerator();
        this.timeProvider = new SystemTimeProvider();
        this.codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
    }

    /**
     * Generate MFA secret for a user
     */
    public String generateSecret() {
        try {
            String secret = secretGenerator.generate();
            logger.info("Generated new MFA secret");
            return secret;
        } catch (Exception e) {
            logger.error("Error generating MFA secret: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate MFA secret", e);
        }
    }

    /**
     * Generate QR code for MFA setup
     */
    public String generateQRCode(String secret, String username) {
        try {
            QrData data = new QrData.Builder()
                .label(username)
                .secret(secret)
                .issuer(ISSUER)
                .algorithm(dev.samstevens.totp.code.HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

            byte[] qrCodeImage = qrGenerator.generate(data);
            String qrCodeBase64 = Base64.getEncoder().encodeToString(qrCodeImage);
            
            logger.info("Generated QR code for user: {}", username);
            return qrCodeBase64;
            
        } catch (QrGenerationException e) {
            logger.error("Error generating QR code for user {}: {}", username, e.getMessage(), e);
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    /**
     * Verify TOTP code
     */
    public boolean verifyCode(String secret, String code) {
        try {
            boolean isValid = codeVerifier.isValidCode(secret, code);
            logger.debug("TOTP code verification result: {}", isValid);
            return isValid;
        } catch (Exception e) {
            logger.error("Error verifying TOTP code: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Generate backup codes for MFA
     */
    public String[] generateBackupCodes() {
        String[] backupCodes = new String[10];
        for (int i = 0; i < 10; i++) {
            backupCodes[i] = generateRandomCode(8);
        }
        logger.info("Generated {} backup codes", backupCodes.length);
        return backupCodes;
    }

    /**
     * Validate backup code
     */
    public boolean verifyBackupCode(String providedCode, String[] backupCodes) {
        if (providedCode == null || backupCodes == null) {
            return false;
        }

        for (int i = 0; i < backupCodes.length; i++) {
            if (providedCode.equals(backupCodes[i])) {
                // Mark code as used by nullifying it
                backupCodes[i] = null;
                logger.info("Backup code verified and marked as used");
                return true;
            }
        }
        
        logger.warn("Invalid backup code provided");
        return false;
    }

    /**
     * Get MFA setup information
     */
    public Map<String, Object> getMFASetupInfo(String secret, String username) {
        Map<String, Object> setupInfo = new HashMap<>();
        setupInfo.put("secret", secret);
        setupInfo.put("qrCode", generateQRCode(secret, username));
        setupInfo.put("backupCodes", generateBackupCodes());
        setupInfo.put("issuer", ISSUER);
        setupInfo.put("algorithm", "SHA1");
        setupInfo.put("digits", 6);
        setupInfo.put("period", 30);
        
        return setupInfo;
    }

    /**
     * Check if MFA is required for user based on role and settings
     */
    public boolean isMFARequired(String role, boolean userMFAEnabled) {
        // Require MFA for admin users or if user has enabled it
        return "ADMIN".equals(role) || userMFAEnabled;
    }

    /**
     * Validate MFA attempt
     */
    public MFAValidationResult validateMFA(String secret, String code, String[] backupCodes) {
        // First try TOTP code
        if (verifyCode(secret, code)) {
            return new MFAValidationResult(true, "totp", "TOTP code verified successfully");
        }
        
        // If TOTP fails, try backup codes
        if (verifyBackupCode(code, backupCodes)) {
            return new MFAValidationResult(true, "backup", "Backup code verified successfully");
        }
        
        return new MFAValidationResult(false, "invalid", "Invalid MFA code");
    }

    /**
     * Generate random backup code
     */
    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            code.append(chars.charAt(index));
        }
        
        return code.toString();
    }

    /**
     * MFA validation result
     */
    public static class MFAValidationResult {
        private final boolean valid;
        private final String method;
        private final String message;

        public MFAValidationResult(boolean valid, String method, String message) {
            this.valid = valid;
            this.method = method;
            this.message = message;
        }

        public boolean isValid() { return valid; }
        public String getMethod() { return method; }
        public String getMessage() { return message; }
    }
}
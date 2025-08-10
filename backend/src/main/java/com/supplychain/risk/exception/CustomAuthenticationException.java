package com.supplychain.risk.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Custom authentication exception for Firebase authentication failures.
 * 
 * This exception is thrown when Firebase token verification fails or
 * when there are issues with the authentication process specific to
 * the Smart Supply Chain Risk Intelligence platform.
 * 
 * @author Supply Chain Risk Intelligence Team
 * @version 1.0
 * @since 2024-01-01
 */
public class CustomAuthenticationException extends AuthenticationException {
    
    /**
     * Error code for the authentication failure.
     */
    private final String errorCode;
    
    /**
     * Additional details about the authentication failure.
     */
    private final Object details;
    
    /**
     * Constructor with message.
     * 
     * @param message the detail message
     */
    public CustomAuthenticationException(String message) {
        super(message);
        this.errorCode = "AUTH_FAILED";
        this.details = null;
    }
    
    /**
     * Constructor with message and cause.
     * 
     * @param message the detail message
     * @param cause the root cause
     */
    public CustomAuthenticationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "AUTH_FAILED";
        this.details = null;
    }
    
    /**
     * Constructor with message and error code.
     * 
     * @param message the detail message
     * @param errorCode the specific error code
     */
    public CustomAuthenticationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.details = null;
    }
    
    /**
     * Constructor with message, error code, and cause.
     * 
     * @param message the detail message
     * @param errorCode the specific error code
     * @param cause the root cause
     */
    public CustomAuthenticationException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = null;
    }
    
    /**
     * Constructor with message, error code, and additional details.
     * 
     * @param message the detail message
     * @param errorCode the specific error code
     * @param details additional error details
     */
    public CustomAuthenticationException(String message, String errorCode, Object details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }
    
    /**
     * Constructor with all parameters.
     * 
     * @param message the detail message
     * @param errorCode the specific error code
     * @param details additional error details
     * @param cause the root cause
     */
    public CustomAuthenticationException(String message, String errorCode, Object details, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = details;
    }
    
    /**
     * Gets the error code associated with this exception.
     * 
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Gets additional details about the authentication failure.
     * 
     * @return the error details
     */
    public Object getDetails() {
        return details;
    }
    
    // Static factory methods for common authentication errors
    
    /**
     * Creates an exception for invalid Firebase token.
     * 
     * @return CustomAuthenticationException for invalid token
     */
    public static CustomAuthenticationException invalidToken() {
        return new CustomAuthenticationException(
            "Invalid or expired Firebase token", 
            "INVALID_TOKEN"
        );
    }
    
    /**
     * Creates an exception for invalid Firebase token with cause.
     * 
     * @param cause the underlying cause
     * @return CustomAuthenticationException for invalid token
     */
    public static CustomAuthenticationException invalidToken(Throwable cause) {
        return new CustomAuthenticationException(
            "Invalid or expired Firebase token", 
            "INVALID_TOKEN", 
            cause
        );
    }
    
    /**
     * Creates an exception for missing authentication token.
     * 
     * @return CustomAuthenticationException for missing token
     */
    public static CustomAuthenticationException missingToken() {
        return new CustomAuthenticationException(
            "Authentication token is missing", 
            "MISSING_TOKEN"
        );
    }
    
    /**
     * Creates an exception for user not found.
     * 
     * @param firebaseUid the Firebase UID that was not found
     * @return CustomAuthenticationException for user not found
     */
    public static CustomAuthenticationException userNotFound(String firebaseUid) {
        return new CustomAuthenticationException(
            "User not found for Firebase UID: " + firebaseUid, 
            "USER_NOT_FOUND",
            firebaseUid
        );
    }
    
    /**
     * Creates an exception for inactive user account.
     * 
     * @param email the email of the inactive user
     * @return CustomAuthenticationException for inactive account
     */
    public static CustomAuthenticationException accountInactive(String email) {
        return new CustomAuthenticationException(
            "User account is inactive: " + email, 
            "ACCOUNT_INACTIVE",
            email
        );
    }
    
    /**
     * Creates an exception for insufficient privileges.
     * 
     * @param requiredRole the required role for the operation
     * @return CustomAuthenticationException for insufficient privileges
     */
    public static CustomAuthenticationException insufficientPrivileges(String requiredRole) {
        return new CustomAuthenticationException(
            "Insufficient privileges. Required role: " + requiredRole, 
            "INSUFFICIENT_PRIVILEGES",
            requiredRole
        );
    }
    
    /**
     * Creates an exception for Firebase service errors.
     * 
     * @param cause the underlying Firebase exception
     * @return CustomAuthenticationException for Firebase errors
     */
    public static CustomAuthenticationException firebaseError(Throwable cause) {
        return new CustomAuthenticationException(
            "Firebase authentication service error", 
            "FIREBASE_ERROR", 
            cause
        );
    }
}
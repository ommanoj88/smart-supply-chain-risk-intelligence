package com.supplychain.risk.exception;

import com.supplychain.risk.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the Smart Supply Chain Risk Intelligence platform.
 * 
 * This class handles all exceptions thrown by the application and provides
 * standardized error responses with appropriate HTTP status codes and messages.
 * 
 * @author Supply Chain Risk Intelligence Team
 * @version 1.0
 * @since 2024-01-01
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handles CustomAuthenticationException.
     * 
     * @param ex the CustomAuthenticationException
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(CustomAuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomAuthenticationException(
            CustomAuthenticationException ex, WebRequest request) {
        
        logger.warn("Custom authentication exception: {} - {}", ex.getErrorCode(), ex.getMessage());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", ex.getErrorCode());
        errorDetails.put("path", request.getDescription(false));
        
        if (ex.getDetails() != null) {
            errorDetails.put("details", ex.getDetails());
        }
        
        ApiResponse<Object> response = ApiResponse.unauthorized(ex.getMessage());
        response.setError(errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Handles general Spring Security AuthenticationException.
     * 
     * @param ex the AuthenticationException
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        
        logger.warn("Authentication exception: {}", ex.getMessage());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", "AUTHENTICATION_FAILED");
        errorDetails.put("path", request.getDescription(false));
        
        ApiResponse<Object> response = ApiResponse.unauthorized("Authentication failed: " + ex.getMessage());
        response.setError(errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Handles Spring Security AccessDeniedException.
     * 
     * @param ex the AccessDeniedException
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        
        logger.warn("Access denied: {}", ex.getMessage());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", "ACCESS_DENIED");
        errorDetails.put("path", request.getDescription(false));
        
        ApiResponse<Object> response = ApiResponse.forbidden("Access denied: " + ex.getMessage());
        response.setError(errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
    
    /**
     * Handles JPA EntityNotFoundException.
     * 
     * @param ex the EntityNotFoundException
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleEntityNotFoundException(
            EntityNotFoundException ex, WebRequest request) {
        
        logger.warn("Entity not found: {}", ex.getMessage());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", "ENTITY_NOT_FOUND");
        errorDetails.put("path", request.getDescription(false));
        
        ApiResponse<Object> response = ApiResponse.notFound(ex.getMessage());
        response.setError(errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handles validation exceptions from @Valid annotations.
     * 
     * @param ex the MethodArgumentNotValidException
     * @param request the web request
     * @return ResponseEntity with validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        logger.warn("Validation failed: {}", ex.getMessage());
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", "VALIDATION_FAILED");
        errorDetails.put("fieldErrors", fieldErrors);
        errorDetails.put("path", request.getDescription(false));
        
        ApiResponse<Object> response = ApiResponse.badRequest("Validation failed");
        response.setError(errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handles constraint violation exceptions.
     * 
     * @param ex the ConstraintViolationException
     * @param request the web request
     * @return ResponseEntity with constraint violation details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        
        logger.warn("Constraint violation: {}", ex.getMessage());
        
        Map<String, String> violations = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                    violation -> violation.getPropertyPath().toString(),
                    ConstraintViolation::getMessage
                ));
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", "CONSTRAINT_VIOLATION");
        errorDetails.put("violations", violations);
        errorDetails.put("path", request.getDescription(false));
        
        ApiResponse<Object> response = ApiResponse.badRequest("Constraint violation");
        response.setError(errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handles HTTP method not supported exceptions.
     * 
     * @param ex the HttpRequestMethodNotSupportedException
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, WebRequest request) {
        
        logger.warn("HTTP method not supported: {}", ex.getMessage());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", "METHOD_NOT_SUPPORTED");
        errorDetails.put("method", ex.getMethod());
        errorDetails.put("supportedMethods", ex.getSupportedMethods());
        errorDetails.put("path", request.getDescription(false));
        
        ApiResponse<Object> response = ApiResponse.badRequest("HTTP method not supported: " + ex.getMethod());
        response.setError(errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }
    
    /**
     * Handles HTTP message not readable exceptions (JSON parsing errors).
     * 
     * @param ex the HttpMessageNotReadableException
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, WebRequest request) {
        
        logger.warn("HTTP message not readable: {}", ex.getMessage());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", "INVALID_JSON");
        errorDetails.put("path", request.getDescription(false));
        
        ApiResponse<Object> response = ApiResponse.badRequest("Invalid JSON format in request body");
        response.setError(errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handles method argument type mismatch exceptions.
     * 
     * @param ex the MethodArgumentTypeMismatchException
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        
        logger.warn("Type mismatch: {}", ex.getMessage());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", "TYPE_MISMATCH");
        errorDetails.put("parameter", ex.getName());
        errorDetails.put("value", ex.getValue());
        errorDetails.put("requiredType", ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        errorDetails.put("path", request.getDescription(false));
        
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(), ex.getName(), 
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        
        ApiResponse<Object> response = ApiResponse.badRequest(message);
        response.setError(errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handles IllegalArgumentException.
     * 
     * @param ex the IllegalArgumentException
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        logger.warn("Illegal argument: {}", ex.getMessage());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", "ILLEGAL_ARGUMENT");
        errorDetails.put("path", request.getDescription(false));
        
        ApiResponse<Object> response = ApiResponse.badRequest(ex.getMessage());
        response.setError(errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handles all other exceptions not specifically handled above.
     * 
     * @param ex the Exception
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(
            Exception ex, WebRequest request) {
        
        logger.error("Unexpected error occurred", ex);
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", "INTERNAL_ERROR");
        errorDetails.put("path", request.getDescription(false));
        
        // In production, don't expose internal error details
        String message = "An unexpected error occurred. Please try again later.";
        
        ApiResponse<Object> response = ApiResponse.internalError(message);
        response.setError(errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
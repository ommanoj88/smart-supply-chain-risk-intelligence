package com.supplychain.risk.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Standardized API response wrapper for all endpoints.
 * 
 * This class provides a consistent response format across all API endpoints
 * in the Smart Supply Chain Risk Intelligence platform, including status,
 * message, data payload, and timestamp information.
 * 
 * @param <T> the type of data being returned
 * @author Supply Chain Risk Intelligence Team
 * @version 1.0
 * @since 2024-01-01
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    /**
     * HTTP status code of the response.
     */
    private int status;
    
    /**
     * Descriptive message about the response.
     */
    private String message;
    
    /**
     * The actual data payload (if successful).
     */
    private T data;
    
    /**
     * Error details (if unsuccessful).
     */
    private Object error;
    
    /**
     * Timestamp when the response was created.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    /**
     * Default constructor.
     */
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructor for successful responses.
     * 
     * @param status HTTP status code
     * @param message response message
     * @param data the data payload
     */
    public ApiResponse(int status, String message, T data) {
        this();
        this.status = status;
        this.message = message;
        this.data = data;
    }
    
    /**
     * Creates a successful response with data.
     * 
     * @param <T> the type of data
     * @param data the data to include
     * @return a successful ApiResponse
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data);
    }
    
    /**
     * Creates a successful response with custom message and data.
     * 
     * @param <T> the type of data
     * @param message custom success message
     * @param data the data to include
     * @return a successful ApiResponse
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }
    
    /**
     * Creates a successful response with no data.
     * 
     * @param message success message
     * @return a successful ApiResponse
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(200, message, null);
    }
    
    /**
     * Creates a created response (HTTP 201).
     * 
     * @param <T> the type of data
     * @param data the created resource
     * @return a created ApiResponse
     */
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, "Resource created successfully", data);
    }
    
    /**
     * Creates a created response with custom message.
     * 
     * @param <T> the type of data
     * @param message custom creation message
     * @param data the created resource
     * @return a created ApiResponse
     */
    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(201, message, data);
    }
    
    /**
     * Creates a bad request error response (HTTP 400).
     * 
     * @param message error message
     * @return an error ApiResponse
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(400);
        response.setMessage(message);
        return response;
    }
    
    /**
     * Creates a bad request error response with details.
     * 
     * @param message error message
     * @param errorDetails error details
     * @return an error ApiResponse
     */
    public static <T> ApiResponse<T> badRequest(String message, Object errorDetails) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(400);
        response.setMessage(message);
        response.setError(errorDetails);
        return response;
    }
    
    /**
     * Creates an unauthorized error response (HTTP 401).
     * 
     * @param message error message
     * @return an error ApiResponse
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(401);
        response.setMessage(message);
        return response;
    }
    
    /**
     * Creates a forbidden error response (HTTP 403).
     * 
     * @param message error message
     * @return an error ApiResponse
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(403);
        response.setMessage(message);
        return response;
    }
    
    /**
     * Creates a not found error response (HTTP 404).
     * 
     * @param message error message
     * @return an error ApiResponse
     */
    public static <T> ApiResponse<T> notFound(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(404);
        response.setMessage(message);
        return response;
    }
    
    /**
     * Creates an internal server error response (HTTP 500).
     * 
     * @param message error message
     * @return an error ApiResponse
     */
    public static <T> ApiResponse<T> internalError(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(500);
        response.setMessage(message);
        return response;
    }
    
    /**
     * Creates an internal server error response with details.
     * 
     * @param message error message
     * @param errorDetails error details
     * @return an error ApiResponse
     */
    public static <T> ApiResponse<T> internalError(String message, Object errorDetails) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(500);
        response.setMessage(message);
        response.setError(errorDetails);
        return response;
    }
    
    // Getters and Setters
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public Object getError() {
        return error;
    }
    
    public void setError(Object error) {
        this.error = error;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "ApiResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", error=" + error +
                ", timestamp=" + timestamp +
                '}';
    }
}
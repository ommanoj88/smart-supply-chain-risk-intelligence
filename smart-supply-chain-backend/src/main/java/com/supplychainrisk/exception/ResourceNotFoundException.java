package com.supplychainrisk.exception;

import org.springframework.http.HttpStatus;

/**
 * Resource not found exception
 */
public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String resource, String identifier) {
        super(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", 
              String.format("%s with identifier '%s' not found", resource, identifier));
    }
}
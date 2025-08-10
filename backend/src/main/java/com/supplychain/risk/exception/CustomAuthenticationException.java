package com.supplychain.risk.exception;

import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationException extends AuthenticationException {
    
    public CustomAuthenticationException(String message) {
        super(message);
    }
    
    public CustomAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
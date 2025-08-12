package com.supplychainrisk.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {
    
    private static final String PASSWORD_PATTERN = 
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
    
    @Override
    public void initialize(StrongPassword constraintAnnotation) {
        // No initialization required
    }
    
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }
        
        // Check minimum length
        if (password.length() < 8) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password must be at least 8 characters long")
                   .addConstraintViolation();
            return false;
        }
        
        // Check maximum length to prevent DoS attacks
        if (password.length() > 128) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password must not exceed 128 characters")
                   .addConstraintViolation();
            return false;
        }
        
        // Check complexity
        if (!pattern.matcher(password).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character (@$!%*?&)")
                   .addConstraintViolation();
            return false;
        }
        
        // Check for common weak passwords
        if (isCommonPassword(password)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password is too common. Please choose a more secure password")
                   .addConstraintViolation();
            return false;
        }
        
        return true;
    }
    
    private boolean isCommonPassword(String password) {
        // List of common weak passwords
        String[] commonPasswords = {
            "password", "123456", "12345678", "qwerty", "abc123", 
            "password123", "admin", "letmein", "welcome", "monkey",
            "dragon", "pass", "password1", "123456789", "football"
        };
        
        String lowerPassword = password.toLowerCase();
        for (String common : commonPasswords) {
            if (lowerPassword.contains(common)) {
                return true;
            }
        }
        
        return false;
    }
}
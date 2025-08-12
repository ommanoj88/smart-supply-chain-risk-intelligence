package com.supplychainrisk.config;

import com.supplychainrisk.service.RateLimitService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Enhanced rate limiting interceptor for API protection with Redis-based distributed rate limiting
 */
public class RateLimitingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingInterceptor.class);
    
    private final RateLimitService rateLimitService;

    public RateLimitingInterceptor(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientId = rateLimitService.getClientIdentifier(request);
        RateLimitService.RateLimitType type = getRateLimitType(request);
        
        boolean allowed = rateLimitService.isAllowed(clientId, type);
        
        if (allowed) {
            // Add rate limit headers for transparency
            long remaining = rateLimitService.getRemainingTokens(clientId, type);
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(remaining));
            response.addHeader("X-Rate-Limit-Type", type.name());
            return true;
        } else {
            // Log rate limit exceeded
            logger.warn("Rate limit exceeded for client: {}, type: {}, path: {}", 
                       clientId, type, request.getRequestURI());
            
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.addHeader("Retry-After", String.valueOf(type.getRefillPeriod().getSeconds()));
            response.addHeader("X-Rate-Limit-Type", type.name());
            response.getWriter().write(String.format(
                "{\"error\":\"Too many requests\",\"message\":\"Rate limit exceeded for %s operations. Please try again in %d seconds.\",\"type\":\"%s\"}", 
                type.name().toLowerCase(), 
                type.getRefillPeriod().getSeconds(), 
                type.name()
            ));
            return false;
        }
    }

    private RateLimitService.RateLimitType getRateLimitType(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Critical operations (stricter limits)
        if (path.contains("/forgot-password") || path.contains("/reset-password")) {
            return RateLimitService.RateLimitType.CRITICAL;
        }
        
        // Authentication operations
        if (path.startsWith("/auth/")) {
            return RateLimitService.RateLimitType.AUTH;
        }
        
        // Admin operations
        if (path.startsWith("/admin/") && ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method))) {
            return RateLimitService.RateLimitType.AUTH;
        }
        
        return RateLimitService.RateLimitType.DEFAULT;
    }
}
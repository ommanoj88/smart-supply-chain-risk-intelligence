package com.supplychainrisk.filter;

import com.supplychainrisk.service.RateLimitingService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Principal;

/**
 * Rate Limiting Filter
 * Applies rate limiting to incoming requests
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

    @Autowired
    private RateLimitingService rateLimitingService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String clientIP = getClientIP(request);
        String userId = getUserId(request);

        // Skip rate limiting for health checks and static resources
        if (shouldSkipRateLimit(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Check IP-based rate limit first
            if (!rateLimitingService.isAllowedForIP(clientIP)) {
                handleRateLimitExceeded(response, "IP rate limit exceeded");
                return;
            }

            // Check user-based rate limit if user is authenticated
            if (userId != null && !rateLimitingService.isAllowed(userId)) {
                handleRateLimitExceeded(response, "User rate limit exceeded");
                return;
            }

            // Check endpoint-specific rate limits
            if (isMLEndpoint(requestURI) && userId != null && 
                !rateLimitingService.isAllowedForMLPrediction(userId)) {
                handleRateLimitExceeded(response, "ML prediction rate limit exceeded");
                return;
            }

            if (isReportEndpoint(requestURI) && userId != null && 
                !rateLimitingService.isAllowedForReportGeneration(userId)) {
                handleRateLimitExceeded(response, "Report generation rate limit exceeded");
                return;
            }

            // Add rate limit headers
            addRateLimitHeaders(response, userId);

            // Continue with request
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            logger.error("Error in rate limiting filter: {}", e.getMessage(), e);
            // Continue with request if rate limiting fails
            filterChain.doFilter(request, response);
        }
    }

    private boolean shouldSkipRateLimit(String requestURI) {
        return requestURI.startsWith("/health") ||
               requestURI.startsWith("/actuator") ||
               requestURI.startsWith("/swagger-ui") ||
               requestURI.startsWith("/v3/api-docs") ||
               requestURI.startsWith("/ws") ||
               requestURI.equals("/");
    }

    private boolean isMLEndpoint(String requestURI) {
        return requestURI.startsWith("/api/ml/");
    }

    private boolean isReportEndpoint(String requestURI) {
        return requestURI.startsWith("/api/reports/");
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

    private String getUserId(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        return principal != null ? principal.getName() : null;
    }

    private void handleRateLimitExceeded(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.getWriter().write(String.format(
            "{\"error\":\"Rate limit exceeded\",\"message\":\"%s\",\"status\":429}", 
            message
        ));
    }

    private void addRateLimitHeaders(HttpServletResponse response, String userId) {
        if (userId != null) {
            try {
                RateLimitingService.RateLimitInfo info = rateLimitingService.getRateLimitInfo(userId);
                response.setHeader("X-Rate-Limit-Total", String.valueOf(info.getTotalCapacity()));
                response.setHeader("X-Rate-Limit-Remaining", String.valueOf(info.getAvailableTokens()));
                response.setHeader("X-Rate-Limit-Reset", String.valueOf(System.currentTimeMillis() + info.getResetPeriod().toMillis()));
            } catch (Exception e) {
                logger.debug("Error adding rate limit headers: {}", e.getMessage());
            }
        }
    }
}
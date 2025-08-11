package com.supplychainrisk.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate Limiting Service
 * Implements API rate limiting using token bucket algorithm
 */
@Service
public class RateLimitingService {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingService.class);

    // Cache for user buckets
    private final Map<String, Bucket> userBuckets = new ConcurrentHashMap<>();
    
    // Cache for IP buckets
    private final Map<String, Bucket> ipBuckets = new ConcurrentHashMap<>();
    
    // Cache for API endpoint buckets
    private final Map<String, Bucket> endpointBuckets = new ConcurrentHashMap<>();

    /**
     * Check if request is allowed for user
     */
    public boolean isAllowed(String userId) {
        return isAllowed(userId, RateLimitType.USER);
    }

    /**
     * Check if request is allowed for IP address
     */
    public boolean isAllowedForIP(String ipAddress) {
        return isAllowed(ipAddress, RateLimitType.IP);
    }

    /**
     * Check if request is allowed for specific endpoint
     */
    public boolean isAllowedForEndpoint(String endpoint, String userId) {
        String key = endpoint + ":" + userId;
        return isAllowed(key, RateLimitType.ENDPOINT);
    }

    /**
     * Check if ML prediction request is allowed
     */
    public boolean isAllowedForMLPrediction(String userId) {
        return isAllowedForEndpoint("ml-prediction", userId);
    }

    /**
     * Check if report generation is allowed
     */
    public boolean isAllowedForReportGeneration(String userId) {
        return isAllowedForEndpoint("report-generation", userId);
    }

    /**
     * Get remaining tokens for user
     */
    public long getRemainingTokens(String userId) {
        Bucket bucket = userBuckets.get(userId);
        if (bucket != null) {
            return bucket.getAvailableTokens();
        }
        return getDefaultUserLimit();
    }

    /**
     * Reset rate limit for user (admin function)
     */
    public void resetUserLimit(String userId) {
        userBuckets.remove(userId);
        logger.info("Reset rate limit for user: {}", userId);
    }

    /**
     * Reset rate limit for IP (admin function)
     */
    public void resetIPLimit(String ipAddress) {
        ipBuckets.remove(ipAddress);
        logger.info("Reset rate limit for IP: {}", ipAddress);
    }

    /**
     * Core rate limiting logic
     */
    private boolean isAllowed(String key, RateLimitType type) {
        try {
            Bucket bucket = getBucket(key, type);
            boolean allowed = bucket.tryConsume(1);
            
            if (!allowed) {
                logger.warn("Rate limit exceeded for {} (type: {})", key, type);
            }
            
            return allowed;
            
        } catch (Exception e) {
            logger.error("Error checking rate limit for key {}: {}", key, e.getMessage(), e);
            // Fail open - allow request if rate limiting fails
            return true;
        }
    }

    /**
     * Get or create bucket for key
     */
    private Bucket getBucket(String key, RateLimitType type) {
        return switch (type) {
            case USER -> userBuckets.computeIfAbsent(key, this::createUserBucket);
            case IP -> ipBuckets.computeIfAbsent(key, this::createIPBucket);
            case ENDPOINT -> endpointBuckets.computeIfAbsent(key, this::createEndpointBucket);
        };
    }

    /**
     * Create bucket for user requests
     */
    private Bucket createUserBucket(String userId) {
        // Default: 100 requests per hour per user
        Bandwidth limit = Bandwidth.classic(getDefaultUserLimit(), Refill.intervally(getDefaultUserLimit(), Duration.ofHours(1)));
        return Bucket4j.builder().addLimit(limit).build();
    }

    /**
     * Create bucket for IP requests
     */
    private Bucket createIPBucket(String ipAddress) {
        // More restrictive: 50 requests per hour per IP
        Bandwidth limit = Bandwidth.classic(50, Refill.intervally(50, Duration.ofHours(1)));
        return Bucket4j.builder().addLimit(limit).build();
    }

    /**
     * Create bucket for endpoint-specific requests
     */
    private Bucket createEndpointBucket(String endpointKey) {
        // Extract endpoint type from key
        String endpoint = endpointKey.split(":")[0];
        
        Bandwidth limit = switch (endpoint) {
            case "ml-prediction" -> 
                // ML predictions: 20 requests per hour per user
                Bandwidth.classic(20, Refill.intervally(20, Duration.ofHours(1)));
            case "report-generation" -> 
                // Report generation: 5 requests per hour per user
                Bandwidth.classic(5, Refill.intervally(5, Duration.ofHours(1)));
            case "auth" -> 
                // Authentication: 10 attempts per 15 minutes
                Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(15)));
            default -> 
                // Default: 30 requests per hour
                Bandwidth.classic(30, Refill.intervally(30, Duration.ofHours(1)));
        };
        
        return Bucket4j.builder().addLimit(limit).build();
    }

    /**
     * Get default user limit
     */
    private long getDefaultUserLimit() {
        return 100; // 100 requests per hour
    }

    /**
     * Check if user has premium rate limits
     */
    public boolean hasPremiumLimits(String userId, String userRole) {
        // Admins and supply managers get higher limits
        return "ADMIN".equals(userRole) || "SUPPLY_MANAGER".equals(userRole);
    }

    /**
     * Create premium bucket for high-tier users
     */
    private Bucket createPremiumUserBucket(String userId) {
        // Premium: 500 requests per hour
        Bandwidth limit = Bandwidth.classic(500, Refill.intervally(500, Duration.ofHours(1)));
        return Bucket4j.builder().addLimit(limit).build();
    }

    /**
     * Get rate limit info for user
     */
    public RateLimitInfo getRateLimitInfo(String userId) {
        Bucket bucket = userBuckets.get(userId);
        if (bucket == null) {
            return new RateLimitInfo(getDefaultUserLimit(), getDefaultUserLimit(), Duration.ofHours(1));
        }
        
        long availableTokens = bucket.getAvailableTokens();
        long totalCapacity = getDefaultUserLimit();
        
        return new RateLimitInfo(totalCapacity, availableTokens, Duration.ofHours(1));
    }

    /**
     * Rate limit types
     */
    private enum RateLimitType {
        USER, IP, ENDPOINT
    }

    /**
     * Rate limit information
     */
    public static class RateLimitInfo {
        private final long totalCapacity;
        private final long availableTokens;
        private final Duration resetPeriod;

        public RateLimitInfo(long totalCapacity, long availableTokens, Duration resetPeriod) {
            this.totalCapacity = totalCapacity;
            this.availableTokens = availableTokens;
            this.resetPeriod = resetPeriod;
        }

        public long getTotalCapacity() { return totalCapacity; }
        public long getAvailableTokens() { return availableTokens; }
        public long getUsedTokens() { return totalCapacity - availableTokens; }
        public Duration getResetPeriod() { return resetPeriod; }
        public double getUsagePercentage() { return ((double) getUsedTokens() / totalCapacity) * 100; }
    }
}
package com.supplychainrisk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimitService {
    
    private static final Logger logger = LoggerFactory.getLogger(RateLimitService.class);
    
    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;
    
    // Fallback in-memory cache when Redis is not available
    private final ConcurrentHashMap<String, TokenBucket> fallbackCache = new ConcurrentHashMap<>();
    
    @Value("${rate.limit.default.capacity:100}")
    private long defaultCapacity;
    
    @Value("${rate.limit.default.refill.tokens:10}")
    private long defaultRefillTokens;
    
    @Value("${rate.limit.auth.capacity:5}")
    private long authCapacity;
    
    @Value("${rate.limit.auth.refill.tokens:1}")
    private long authRefillTokens;
    
    @Value("${rate.limit.critical.capacity:2}")
    private long criticalCapacity;
    
    @Value("${rate.limit.critical.refill.tokens:1}")
    private long criticalRefillTokens;

    @Value("${redis.enabled:false}")
    private boolean redisEnabled;

    private boolean redisErrorLogged = false;

    public enum RateLimitType {
        DEFAULT(Duration.ofMinutes(1)),
        AUTH(Duration.ofMinutes(1)),
        CRITICAL(Duration.ofMinutes(5)); // For password reset, etc.
        
        private final Duration refillPeriod;
        
        RateLimitType(Duration refillPeriod) {
            this.refillPeriod = refillPeriod;
        }
        
        public Duration getRefillPeriod() {
            return refillPeriod;
        }
    }
    
    public boolean isAllowed(String key, RateLimitType type) {
        try {
            if (redisTemplate != null && redisEnabled) {
                return isAllowedRedis(key, type);
            } else {
                if (!redisErrorLogged && !redisEnabled) {
                    logger.info("Using in-memory rate limiting (Redis disabled)");
                    redisErrorLogged = true;
                }
                return isAllowedMemory(key, type);
            }
        } catch (Exception e) {
            logger.error("Error checking rate limit for key {}: {}", key, e.getMessage());
            // Fail open - allow request if there's an error
            return true;
        }
    }
    
    private boolean isAllowedRedis(String key, RateLimitType type) {
        try {
            String redisKey = buildRedisKey(key, type);
            String currentCountStr = redisTemplate.opsForValue().get(redisKey);
            long currentCount = currentCountStr != null ? Long.parseLong(currentCountStr) : 0;
            
            if (currentCount >= getCapacity(type)) {
                return false;
            }
            
            // Increment counter
            long newCount = redisTemplate.opsForValue().increment(redisKey, 1);
            
            // Set expiration on first access
            if (newCount == 1) {
                redisTemplate.expire(redisKey, type.getRefillPeriod());
            }
            
            return newCount <= getCapacity(type);
            
        } catch (Exception e) {
            if (!redisErrorLogged) {
                logger.warn("Redis connection failed, falling back to in-memory rate limiting: {}", e.getMessage());
                redisErrorLogged = true;
            }
            return isAllowedMemory(key, type);
        }
    }
    
    private boolean isAllowedMemory(String key, RateLimitType type) {
        String cacheKey = buildRedisKey(key, type);
        TokenBucket bucket = fallbackCache.computeIfAbsent(cacheKey, k -> 
            new TokenBucket(getCapacity(type), getRefillTokens(type), type.getRefillPeriod()));
        
        return bucket.tryConsume(1);
    }
    
    public long getRemainingTokens(String key, RateLimitType type) {
        try {
            if (redisTemplate != null) {
                String redisKey = buildRedisKey(key, type);
                String currentCountStr = redisTemplate.opsForValue().get(redisKey);
                long currentCount = currentCountStr != null ? Long.parseLong(currentCountStr) : 0;
                return Math.max(0, getCapacity(type) - currentCount);
            } else {
                String cacheKey = buildRedisKey(key, type);
                TokenBucket bucket = fallbackCache.get(cacheKey);
                return bucket != null ? bucket.getAvailableTokens() : getCapacity(type);
            }
        } catch (Exception e) {
            logger.error("Error getting remaining tokens for key {}: {}", key, e.getMessage());
            return getCapacity(type);
        }
    }
    
    public void resetRateLimit(String key, RateLimitType type) {
        try {
            String redisKey = buildRedisKey(key, type);
            if (redisTemplate != null) {
                redisTemplate.delete(redisKey);
            }
            fallbackCache.remove(redisKey);
            logger.info("Rate limit reset for key: {}, type: {}", key, type);
        } catch (Exception e) {
            logger.error("Error resetting rate limit for key {}: {}", key, e.getMessage());
        }
    }
    
    private String buildRedisKey(String key, RateLimitType type) {
        return String.format("rate_limit:%s:%s", type.name().toLowerCase(), key);
    }
    
    private long getCapacity(RateLimitType type) {
        return switch (type) {
            case AUTH -> authCapacity;
            case CRITICAL -> criticalCapacity;
            default -> defaultCapacity;
        };
    }
    
    private long getRefillTokens(RateLimitType type) {
        return switch (type) {
            case AUTH -> authRefillTokens;
            case CRITICAL -> criticalRefillTokens;
            default -> defaultRefillTokens;
        };
    }
    
    // Utility method to get client identifier
    public String getClientIdentifier(jakarta.servlet.http.HttpServletRequest request) {
        // Try to get user ID from authentication context first
        try {
            org.springframework.security.core.Authentication auth = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof com.supplychainrisk.entity.User user) {
                return "user:" + user.getId();
            }
        } catch (Exception e) {
            // Fall back to IP-based identification
        }
        
        // Use IP address as fallback
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return "ip:" + xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return "ip:" + xRealIp;
        }
        
        return "ip:" + request.getRemoteAddr();
    }
    
    // Simple in-memory token bucket implementation
    private static class TokenBucket {
        private final long capacity;
        private final long refillTokens;
        private final Duration refillPeriod;
        private long tokens;
        private long lastRefillTime;
        
        public TokenBucket(long capacity, long refillTokens, Duration refillPeriod) {
            this.capacity = capacity;
            this.refillTokens = refillTokens;
            this.refillPeriod = refillPeriod;
            this.tokens = capacity;
            this.lastRefillTime = System.currentTimeMillis();
        }
        
        public synchronized boolean tryConsume(long tokensRequested) {
            refill();
            if (tokens >= tokensRequested) {
                tokens -= tokensRequested;
                return true;
            }
            return false;
        }
        
        public synchronized long getAvailableTokens() {
            refill();
            return tokens;
        }
        
        private void refill() {
            long now = System.currentTimeMillis();
            long timePassed = now - lastRefillTime;
            long refillPeriodMs = refillPeriod.toMillis();
            
            if (timePassed >= refillPeriodMs) {
                long periodsElapsed = timePassed / refillPeriodMs;
                long tokensToAdd = periodsElapsed * refillTokens;
                tokens = Math.min(capacity, tokens + tokensToAdd);
                lastRefillTime = now;
            }
        }
    }
}
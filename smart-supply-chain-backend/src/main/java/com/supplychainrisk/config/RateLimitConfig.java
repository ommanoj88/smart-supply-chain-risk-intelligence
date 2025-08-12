package com.supplychainrisk.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiting configuration for API protection
 */
@Configuration
public class RateLimitConfig implements WebMvcConfigurer {

    @Value("${rate.limit.default.capacity:100}")
    private long defaultCapacity;

    @Value("${rate.limit.default.refill.tokens:10}")
    private long defaultRefillTokens;

    @Value("${rate.limit.default.refill.period:PT1M}")
    private Duration defaultRefillPeriod;

    @Value("${rate.limit.auth.capacity:5}")
    private long authCapacity;

    @Value("${rate.limit.auth.refill.tokens:1}")
    private long authRefillTokens;

    @Value("${rate.limit.auth.refill.period:PT1M}")
    private Duration authRefillPeriod;

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Bean
    public Bucket defaultBucket() {
        Bandwidth bandwidth = Bandwidth.classic(defaultCapacity, 
                Refill.intervally(defaultRefillTokens, defaultRefillPeriod));
        return Bucket4j.builder()
                .addLimit(bandwidth)
                .build();
    }

    @Bean
    public Bucket authBucket() {
        Bandwidth bandwidth = Bandwidth.classic(authCapacity, 
                Refill.intervally(authRefillTokens, authRefillPeriod));
        return Bucket4j.builder()
                .addLimit(bandwidth)
                .build();
    }

    public Bucket createNewBucket(String bucketType) {
        if ("auth".equals(bucketType)) {
            return authBucket();
        }
        return defaultBucket();
    }

    public Bucket resolveBucket(String key, String bucketType) {
        return cache.computeIfAbsent(key, k -> createNewBucket(bucketType));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RateLimitingInterceptor(this))
                .addPathPatterns("/api/**", "/auth/**");
    }
}
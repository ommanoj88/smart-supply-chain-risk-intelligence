package com.supplychainrisk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis configuration for caching and session management
 */
@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);
        return new LettuceConnectionFactory(redisConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Use String serializer for keys
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // Use JSON serializer for values
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // Default cache configuration
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30)) // Default 30 minutes TTL
            .serializeKeysWith(org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        // Specific cache configurations
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Supplier cache - 1 hour TTL
        cacheConfigurations.put("suppliers", defaultCacheConfig
            .entryTtl(Duration.ofHours(1)));
        
        // Shipment cache - 15 minutes TTL
        cacheConfigurations.put("shipments", defaultCacheConfig
            .entryTtl(Duration.ofMinutes(15)));
        
        // ML predictions cache - 5 minutes TTL
        cacheConfigurations.put("ml-predictions", defaultCacheConfig
            .entryTtl(Duration.ofMinutes(5)));
        
        // Risk scores cache - 10 minutes TTL
        cacheConfigurations.put("risk-scores", defaultCacheConfig
            .entryTtl(Duration.ofMinutes(10)));
        
        // Dashboard metrics cache - 2 minutes TTL
        cacheConfigurations.put("dashboard-metrics", defaultCacheConfig
            .entryTtl(Duration.ofMinutes(2)));
        
        // User sessions cache - 24 hours TTL
        cacheConfigurations.put("user-sessions", defaultCacheConfig
            .entryTtl(Duration.ofHours(24)));

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(defaultCacheConfig)
            .withInitialCacheConfigurations(cacheConfigurations)
            .build();
    }
}
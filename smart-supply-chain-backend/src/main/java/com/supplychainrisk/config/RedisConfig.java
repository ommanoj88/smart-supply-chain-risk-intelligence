package com.supplychainrisk.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${redis.enabled:false}")
    private boolean redisEnabled;

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Bean
    @ConditionalOnProperty(name = "redis.enabled", havingValue = "true")
    public RedisConnectionFactory redisConnectionFactory() {
        try {
            LettuceConnectionFactory factory = new LettuceConnectionFactory(redisHost, redisPort);
            factory.setValidateConnection(true);
            logger.info("Redis connection factory created for {}:{}", redisHost, redisPort);
            return factory;
        } catch (Exception e) {
            logger.error("Failed to create Redis connection factory: {}", e.getMessage());
            return null;
        }
    }

    @Bean
    @ConditionalOnProperty(name = "redis.enabled", havingValue = "true")
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        try {
            StringRedisTemplate template = new StringRedisTemplate(connectionFactory);
            // Test the connection
            template.getConnectionFactory().getConnection().ping();
            logger.info("Redis connection successful. Distributed rate limiting enabled.");
            return template;
        } catch (Exception e) {
            logger.warn("Redis connection failed: {}. Rate limiting will use in-memory fallback.", e.getMessage());
            return null;
        }
    }
}

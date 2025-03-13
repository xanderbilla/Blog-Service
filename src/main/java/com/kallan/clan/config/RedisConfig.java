package com.kallan.clan.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;

import java.time.Duration;

@Configuration
public class RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10)) // Set cache expiration
            .disableCachingNullValues();

        logger.info("Cache Manager initialized with a TTL of 10 minutes.");

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .transactionAware()
            .build();
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        // Use the service name 'redis' as the hostname
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration("redis", 6379);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        factory.afterPropertiesSet();
        try {
            factory.getConnection().ping();
            logger.info("Redis is connected to {}:{}", redisStandaloneConfiguration.getHostName(), redisStandaloneConfiguration.getPort());
        } catch (Exception e) {
            logger.error("Redis is not connected.");
        }
        return factory;
    }
}

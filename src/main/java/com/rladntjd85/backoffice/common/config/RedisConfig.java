package com.rladntjd85.backoffice.common.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching // 캐싱 기능을 활성화합니다.
public class RedisConfig {

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        // t3.small(2GB) 환경이므로 직렬화와 TTL 설정을 꼼꼼히 관리합니다.
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)) // 캐시 유지 시간 30분 설정
                .disableCachingNullValues() // null 값은 캐싱하지 않음
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }
}
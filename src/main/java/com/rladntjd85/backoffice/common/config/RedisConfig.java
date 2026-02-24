package com.rladntjd85.backoffice.common.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 1. ObjectMapper를 직접 로컬 변수로 선언하여 설정을 완벽히 제어합니다.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 모든 객체에 대해 클래스 정보를 포함하도록 검증기 설정
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Object.class)
                .build();

        objectMapper.activateDefaultTyping(
                ptv,
                ObjectMapper.DefaultTyping.EVERYTHING, // 더 광범위한 타입 정보 포함
                JsonTypeInfo.As.PROPERTY
        );

        // 2. 이 ObjectMapper를 사용하는 전용 시리얼라이저 생성
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // 3. 캐시 설정 적용
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(1))
                .disableCachingNullValues()
                // 팩트 체크: 명시적으로 serializer를 다시 한번 정의하여 주입합니다.
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));

        // 4. 설정이 입혀진 CacheManager 반환
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}
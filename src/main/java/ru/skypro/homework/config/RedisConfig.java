package ru.skypro.homework.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Конфигурация кэширования на базе Redis.
 * <p>
 * Включает аннотационное кэширование ({@code @EnableCaching}) и создаёт
 * {@link RedisCacheManager} с едиными настройками для всех кэшей приложения:
 * </p>
 * <ul>
 *     <li>TTL — 10 минут</li>
 *     <li>ключи сериализуются в {@link StringRedisSerializer}</li>
 *     <li>значения — в {@link GenericJackson2JsonRedisSerializer}</li>
 *     <li>{@code null}-значения не кэшируются</li>
 * </ul>
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * Создаёт менеджер кэшей с подключением к Redis.
     * <p>
     * Все кэши по умолчанию используют общий конфиг: TTL 10 минут,
     * JSON-сериализацию значений и строковые ключи.
     * </p>
     *
     * @param connectionFactory фабрика подключений к Redis (предоставляется Spring Boot)
     * @return {@link RedisCacheManager} с настроенным TTL и сериализацией
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
                );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}

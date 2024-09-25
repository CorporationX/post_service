package faang.school.postservice.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RedisManager {
    private final JedisConnectionFactory jedisConnectionFactory;
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.data.redis.time-to-live}")
    private long timeToLive;

    @Bean
    public CacheManager cacheManager() {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(Object.class)));
        return RedisCacheManager.builder(jedisConnectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }

    public void cachePostAuthor(Long authorId) {
        String cacheKey = "postAuthor";
        redisTemplate.opsForValue().set(cacheKey, authorId, Duration.ofHours(timeToLive));
    }
}

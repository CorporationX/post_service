package faang.school.postservice.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisCacheConfig {

    @Value("${spring.data.redis.ttl}")
    private long ttl;

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration("PostInRedis", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(ttl)))
                .withCacheConfiguration("AuthorPostInRedis", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(ttl)));
    }
}

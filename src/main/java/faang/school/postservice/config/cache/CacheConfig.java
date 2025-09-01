package faang.school.postservice.config.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CacheConfig {

    private final PostCacheProperties postCacheProperties;

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        configMap.put(postCacheProperties.getPrefix(), RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(postCacheProperties.getTtlHours()))
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()
                        )
                ));

        return RedisCacheManager.builder(redisConnectionFactory)
                .withInitialCacheConfigurations(configMap)
                .build();
    }

}

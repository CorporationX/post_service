package faang.school.postservice.config.redis;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import faang.school.postservice.config.MainConfig;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RedisCacheConfig {
    private final MainConfig mainConfig;

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(mainConfig.getTtlMins()))
            .disableCachingNullValues();
        
        RedisCacheManager redisCacheManager = RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(config)
            .withInitialCacheConfigurations(getCacheConfigurations())
            .build();

        return redisCacheManager;
    }

    private Map<String, RedisCacheConfiguration> getCacheConfigurations() {
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        cacheConfigs.put(mainConfig.getFeedKey(), RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(mainConfig.getFeedTtlMins())));
        cacheConfigs.put(mainConfig.getPostsKey(), RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(mainConfig.getPostsTtlMins())));
        return cacheConfigs;
    }

}

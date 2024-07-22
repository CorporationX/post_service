package faang.school.postservice.config.redis;

import faang.school.postservice.property.RedisCacheProperty;
import faang.school.postservice.property.RedisLockRegistryProperty;
import faang.school.postservice.redis.cache.entity.AuthorCache;
import faang.school.postservice.redis.cache.entity.CommentCache;
import faang.school.postservice.redis.cache.entity.FeedCache;
import faang.school.postservice.redis.cache.entity.PostCache;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.ExpirableLockRegistry;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RedisCacheConfig {

    private final RedisCacheProperty redisCacheProperty;
    private final RedisLockRegistryProperty redisLockRegistryProperty;

    @Bean
    public ExpirableLockRegistry expirableLockRegistry(RedisConnectionFactory redisConnectionFactory) {

        return new RedisLockRegistry(redisConnectionFactory, redisLockRegistryProperty.getPostLockKey(),
                redisLockRegistryProperty.getReleaseTimeDurationMillis());
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.of(redisCacheProperty.getDefaultTtl(), ChronoUnit.SECONDS));
        return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration);
    }

    @SuppressWarnings("unused")
    public class RedisKeyspaceConfiguration extends KeyspaceConfiguration {

        private static final List<Class<?>> entityClasses = List.of(
                PostCache.class,
                AuthorCache.class,
                CommentCache.class,
                FeedCache.class
        );

        @Override
        protected @NonNull Iterable<KeyspaceSettings> initialConfiguration() {

            return entityClasses.stream().map(this::getKeyspaceSettings).toList();
        }

        @Override
        public @NonNull KeyspaceSettings getKeyspaceSettings(@NonNull Class<?> type) {

            String cacheName = type.getAnnotation(RedisHash.class).value();

            KeyspaceSettings keyspaceSettings = new KeyspaceSettings(type, cacheName);
            keyspaceSettings.setTimeToLive(redisCacheProperty.getCacheSettings().get(cacheName).getTtl());

            return keyspaceSettings;
        }
    }
}

package faang.school.postservice.config.redis;

import faang.school.postservice.property.RedisCacheProperty;
import faang.school.postservice.redis.cache.entity.AuthorCache;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RedisCacheConfig {

    private final RedisCacheProperty redisCacheProperty;

    private static final List<Class<?>> entityClasses = List.of(
            AuthorCache.class
    );

    public class RedisKeyspaceConfiguration extends KeyspaceConfiguration {

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


package faang.school.postservice.config.redis;

import faang.school.postservice.property.RedisCacheProperty;
import faang.school.postservice.property.RedisLockRegistryProperty;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.ExpirableLockRegistry;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisCacheConfig {

    @Value("${spring.data.redis.lock-registry.lockSettings.default.name}")
    private String defaultLockSettings;

    @Value("${spring.data.redis.lock-registry.lockSettings.feed.name}")
    private String feedLockSettings;

    private final RedisLockRegistryProperty redisLockRegistryProperty;
    private final RedisCacheProperty redisCacheProperty;
    private static final List<Class<?>> entityClasses = new ArrayList<>();

    @Bean
    public ExpirableLockRegistry expirableLockRegistry(RedisConnectionFactory redisConnectionFactory) {

        return new RedisLockRegistry(redisConnectionFactory, redisLockRegistryProperty.getLockSettings().get(defaultLockSettings).getPostLockKey(),
                redisLockRegistryProperty.getLockSettings().get(defaultLockSettings).getReleaseTimeDurationMillis());
    }

    @Bean
    public ExpirableLockRegistry feedLockRegistry(RedisConnectionFactory redisConnectionFactory){
        return new RedisLockRegistry(redisConnectionFactory, redisLockRegistryProperty.getLockSettings().get(feedLockSettings).getPostLockKey(),
                redisLockRegistryProperty.getLockSettings().get(feedLockSettings).getReleaseTimeDurationMillis());
    }

    @Bean
    public ZSetOperations<String, Long> redisFeedZSetOps(RedisTemplate<String, Long> feedRedisTemplate){
        return feedRedisTemplate.opsForZSet();
    }

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

    @PostConstruct
    private void findAllCacheEntities() {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(redisCacheProperty.getPathToEntities().replaceAll("[.]", "/"));
        if(stream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            reader.lines()
                    .filter(line -> line.endsWith(".class"))
                    .map(line -> getClass(line, redisCacheProperty.getPathToEntities()))
                    .filter(cls -> cls.isAnnotationPresent(RedisHash.class))
                    .forEach(entityClasses::add);
        }
    }

    private Class<?> getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            String message = "The entity's Cache Redis class " +
                    String.format("with className: %s and packageName: %s", className, packageName)
                    + " could not be found";
            log.error(message, e);
            throw new RuntimeException(message);
        }
    }
}


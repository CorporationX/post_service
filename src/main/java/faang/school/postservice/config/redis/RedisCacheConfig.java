package faang.school.postservice.config.redis;

import faang.school.postservice.property.RedisCacheProperty;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisCacheConfig {

    private final RedisCacheProperty redisCacheProperty;
    private static final List<Class<?>> entityClasses = new ArrayList<>();

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
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, redisCacheProperty.getPathToEntities()))
                .filter(cls -> cls.isAnnotationPresent(RedisHash.class))
                .forEach(entityClasses::add);
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


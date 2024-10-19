package faang.school.postservice.config.redis;

import faang.school.postservice.cache.model.PostRedis;
import faang.school.postservice.cache.model.UserRedis;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.lang.NonNull;

@Configuration
@EnableRedisRepositories(keyspaceConfiguration = RedisConfig.MyKeyspaceConfiguration.class)
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.cache.post.ttl-hours}")
    private long postTtlHours;

    @Value("${spring.data.redis.cache.user.ttl-hours}")
    private long userTtlHours;

    @Value("${spring.data.redis.lock-registry.key}")
    private String registryKey;

    @Value("${spring.data.redis.lock-registry.ttl-millis}")
    private long lockTtlMillis;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public RedisLockRegistry redisLockRegistry(JedisConnectionFactory factory) {
        return new RedisLockRegistry(factory, registryKey, lockTtlMillis);
    }

    public class MyKeyspaceConfiguration extends KeyspaceConfiguration {
        @Override
        public boolean hasSettingsFor(@NonNull Class<?> type) {
            return true;
        }

        @Override
        public @NonNull KeyspaceSettings getKeyspaceSettings(@NonNull Class<?> type) {
            long secondsInHour = 3600L;

            KeyspaceSettings keyspacePostSettings = new KeyspaceSettings(PostRedis.class, "Post");
            KeyspaceSettings keyspaceUserSettings = new KeyspaceSettings(UserRedis.class, "User");
            keyspacePostSettings.setTimeToLive(postTtlHours * secondsInHour);
            keyspaceUserSettings.setTimeToLive(userTtlHours * secondsInHour);

            return keyspacePostSettings;
        }
    }
}

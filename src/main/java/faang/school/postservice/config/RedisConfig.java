package faang.school.postservice.config;

import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.model.redis.UserRedis;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

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

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }

    public class MyKeyspaceConfiguration extends KeyspaceConfiguration {
        @Override
        public boolean hasSettingsFor(Class<?> type) {
            return true;
        }

        @Override
        public KeyspaceSettings getKeyspaceSettings(Class<?> type) {
            long secondsInHour = 3600L;

            KeyspaceSettings keyspacePostSettings = new KeyspaceSettings(PostRedis.class, "Post");
            KeyspaceSettings keyspaceUserSettings = new KeyspaceSettings(UserRedis.class, "User");
            keyspacePostSettings.setTimeToLive(postTtlHours * secondsInHour);
            keyspaceUserSettings.setTimeToLive(userTtlHours * secondsInHour);

            return keyspacePostSettings;
        }
    }
}

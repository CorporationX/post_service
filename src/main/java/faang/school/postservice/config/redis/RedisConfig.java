package faang.school.postservice.config.redis;


import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@EnableCaching
@Configuration
@EnableRedisRepositories(
        enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP,
        keyspaceConfiguration = RedisConfig.MyKeyspaceConfiguration.class
)
public class RedisConfig {
    @Value("${spring.data.redis.port}")
    private int port;
    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.channels.post_view_event_channel.name}")
    private String postViewEventChanel;
    @Value("${spring.data.redis.cache-ttl}")
    private long redisCacheExpiration;
    @Value("${spring.data.redis.data-ttl}")
    private long redisDataExpiration;

    @Bean
    public ChannelTopic postViewEventTopic() {
        return new ChannelTopic(postViewEventChanel);
    }

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    @Bean
    RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.of(redisCacheExpiration, ChronoUnit.SECONDS));
        return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration);
    }

    public class MyKeyspaceConfiguration extends KeyspaceConfiguration {
        @Override
        public boolean hasSettingsFor(Class<?> type) {
            return true;
        }

        @Override
        public KeyspaceSettings getKeyspaceSettings(Class<?> type) {

            KeyspaceSettings keyspacePostSettings = new KeyspaceSettings(PostDto.class, "Posts");
            KeyspaceSettings keyspaceUserSettings = new KeyspaceSettings(UserDto.class, "Users");
            keyspacePostSettings.setTimeToLive(redisDataExpiration);
            keyspaceUserSettings.setTimeToLive(redisDataExpiration);

            return keyspacePostSettings;
        }
    }
}

package faang.school.postservice.config;

import faang.school.postservice.dto.feed.Feed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
@EnableRedisRepositories(
        enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP,
        keyspaceConfiguration = RedisConfig.CustomSpaceConfiguration.class)
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.channels.comment_channel.name}")
    private String commentEventName;

    @Value("${spring.data.redis.key-spaces.feed.name}")
    private String feedKeySpace;

    @Value("${spring.data.redis.key-spaces.feed.ttl}")
    private long feedTTL;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        log.info("redis start work at port {}", port);
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.lockingRedisCacheWriter(redisConnectionFactory);
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        return new RedisCacheManager(redisCacheWriter, cacheConfiguration);
    }

    @Bean
    public ChannelTopic commentTopic(){
        return new ChannelTopic(commentEventName);
    }

    public class CustomSpaceConfiguration extends KeyspaceConfiguration {
        @Override
        public KeyspaceSettings getKeyspaceSettings(Class<?> type) {
            KeyspaceSettings feedSpaceSettings = new KeyspaceSettings(Feed.class, feedKeySpace);
            feedSpaceSettings.setTimeToLive(TimeUnit.DAYS.toSeconds(feedTTL));
            return feedSpaceSettings;
        }
    }
}
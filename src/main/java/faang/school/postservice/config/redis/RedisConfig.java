package faang.school.postservice.config.redis;

import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisProperties redisProperties;

    @Bean
    public ChannelTopic likeTopic() {
        return new ChannelTopic(redisProperties.getChannels().getLikesChannel());
    }

    @Bean
    public ChannelTopic commentTopic() {
        return new ChannelTopic(redisProperties.getChannels().getCommentsChannel());
    }

    @Bean
    public RedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }

    public class MyKeyspaceConfiguration extends KeyspaceConfiguration {
        @Override
        protected Iterable<KeyspaceSettings> initialConfiguration() {
            RedisProperties.Cache userCache = redisProperties.getUserCache();

            KeyspaceSettings userKeyspaceSettings = new KeyspaceSettings(UserDto.class, userCache.getKey());
            userKeyspaceSettings.setTimeToLive(TimeUnit.DAYS.toSeconds(userCache.getTtl()));

            return List.of(userKeyspaceSettings);
        }
    }
}

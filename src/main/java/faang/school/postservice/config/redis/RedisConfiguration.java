package faang.school.postservice.config.redis;

import faang.school.postservice.dto.like.LikeEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {

    private final RedisPropertiesConfiguration propertiesConfig;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic(propertiesConfig.getLikeEvents());
    }

    @Bean
    public RedisTemplate<String, LikeEventDto> redisTemplate() {
        RedisTemplate<String, LikeEventDto> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }
}

package faang.school.postservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {

    private final RedisProperties redisProperties;
    private final ObjectMapper objectMapper;

    @Bean
    public ChannelTopic likePostChannel() {
        return new ChannelTopic(redisProperties.getChannels().getLikePostChannel().getName());
    }

    @Bean
    public ChannelTopic commentEventChannel() {
        return new ChannelTopic(redisProperties.getChannels().getCommentChannel().getName());
    }

    @Bean
    public ChannelTopic achievementEventTopic() {
        return new ChannelTopic(redisProperties.getChannels().getLikePostChannel().getName());
    }

    @Bean
    public ChannelTopic publishedCommentEventTopic() {
        return new ChannelTopic(redisProperties.getChannels().getNewCommentChannel().getName());
    private final RedisProperties propertiesConfig;
    private final ObjectMapper objectMapper;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public ChannelTopic likeEventsTopic() {
        return new ChannelTopic(propertiesConfig.getChannels().getLikeEventsChannel().getName());
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        return template;
    }

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }
}

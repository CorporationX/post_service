package faang.school.postservice.config;

import faang.school.postservice.dto.like.LikePostEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.channels.post_like_channel.name}")
    private String postLikeChannel;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config =
                new RedisStandaloneConfiguration(redisHost, redisPort);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public Jackson2JsonRedisSerializer<LikePostEvent> likeEventJsonSerializer() {
        return new Jackson2JsonRedisSerializer<>(LikePostEvent.class);
    }

    @Bean
    public RedisTemplate<String, LikePostEvent> redisTemplate() {
        RedisTemplate<String, LikePostEvent> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setValueSerializer(likeEventJsonSerializer());
        return redisTemplate;
    }

    @Bean
    public ChannelTopic postLikeChannel() {
        return new ChannelTopic(postLikeChannel);
    }
}
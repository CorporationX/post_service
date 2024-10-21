package faang.school.postservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.dto.post.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.channels.like-channel.name}")
    private String likeEvent;
    @Value("${spring.data.redis.channels.comment-event-channel.name}")
    private String commentEvent;

    @Value("${spring.data.redis.channels.user-ban.name}")
    private String userBanEvent;

    @Value("${spring.data.redis.channels.post-channel.name}")
    private String postEventChannel;

    @Value("${spring.data.redis.channels.ad-bought-channel.name}")
    private String adBoughtEvent;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, List<PostDto>> redisHashtagTemplate(JedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, List<PostDto>> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        return redisTemplate;
    }

    @Bean
    public ChannelTopic likeTopic() {
        return new ChannelTopic(likeEvent);
    }

    @Bean
    public ChannelTopic userBanTopic() {
        return new ChannelTopic(userBanEvent);
    }

    @Bean
    public ChannelTopic commentTopic() {
        return new ChannelTopic(commentEvent);
    }

    @Bean
    public ChannelTopic postEventTopic() {
        return new ChannelTopic(postEventChannel);
    }

    @Bean
    public ChannelTopic adBoughtEventTopic() {
        return new ChannelTopic(adBoughtEvent);
    }
}

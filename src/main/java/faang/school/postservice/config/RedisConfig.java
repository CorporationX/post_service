package faang.school.postservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.publisher.MessagePublisher;
import faang.school.postservice.publisher.LikePostPublisher;
import faang.school.postservice.mapper.LikeEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisCredentials credentials;

    @Value("${spring.data.redis.channels.like}")
    private String likePostTopic;

    @Bean("postChannelTopic")
    public ChannelTopic postChannelTopic() {
        return new ChannelTopic(credentials.getChannels().getPost());
    }

    @Bean
    ChannelTopic likePostTopic() {
        return new ChannelTopic(likePostTopic);
    }

    @Bean
    MessagePublisher redisLikePostPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic likePostTopic,
                                    ObjectMapper objectMapper, LikeEventMapper likeEventMapper) {
        return new LikePostPublisher(redisTemplate, likePostTopic, objectMapper, likeEventMapper);
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(credentials.getHost(), credentials.getPort());
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}

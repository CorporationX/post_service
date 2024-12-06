package faang.school.postservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.publisher.redis.LikeEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
@EnableRedisRepositories
public class RedisConfig {
    private final RedisDto redisDto;

    @Value("${spring.data.redis.channels.like_channel}")
    private String likeChannelName;

    @Value("${spring.data.redis.channels.comment_channel}")
    private String commentChannelName;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    public static class MyKeyspaceConfiguration extends KeyspaceConfiguration {

        @Override
        protected Iterable<KeyspaceSettings> initialConfiguration() {
            return Collections.singleton(new KeyspaceSettings(PostDto.class, "people"));
        }
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisDto.getHost(), redisDto.getPort());
        return new JedisConnectionFactory(redisConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper()));
        return template;
    }

    @Bean
    public ChannelTopic likeEventTopic() {
        return new ChannelTopic(likeChannelName);
    }

    @Bean
    public ChannelTopic commentEventTopic() {
        return new ChannelTopic(commentChannelName);
    }

    @Bean
    public LikeEventPublisher likeEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic likeEventTopic) {
        return new LikeEventPublisher(redisTemplate, likeEventTopic);
    }

}

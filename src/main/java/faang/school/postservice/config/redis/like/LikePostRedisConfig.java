package faang.school.postservice.config.redis.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.MessagePublisher;
import faang.school.postservice.mapper.LikeEventMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericToStringSerializer;


@Configuration
public class LikePostRedisConfig {

    @Value("${spring.data.redis.topic.like}")
    private String likePostTopic;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        return template;
    }

    @Bean
    ChannelTopic likePostTopic() {
        return new ChannelTopic(likePostTopic);
    }

    @Bean
    MessagePublisher redisPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic likePostTopic,
                                    ObjectMapper objectMapper, LikeEventMapper likeEventMapper) {
        return new LikePostPublisher(redisTemplate, likePostTopic, objectMapper, likeEventMapper);
    }
}
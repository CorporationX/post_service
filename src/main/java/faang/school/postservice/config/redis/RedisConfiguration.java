package faang.school.postservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.mapper.CommentEventMapper;
import faang.school.postservice.mapper.LikeEventMapper;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.publisher.LikePostPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

@Configuration
public class RedisConfiguration {
    @Value("${spring.data.redis.topic.comment_achievement}")
    private String commentTopic;
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
    MessagePublisher LikePostPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic likePostTopic,
                                       ObjectMapper objectMapper, LikeEventMapper likeEventMapper) {
        return new LikePostPublisher(redisTemplate, likePostTopic, objectMapper, likeEventMapper);
    }

    @Bean
    ChannelTopic commentTopic() {
        return new ChannelTopic(commentTopic);
    }

    @Bean
    MessagePublisher CommentEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic commentTopic,
                                           ObjectMapper objectMapper, CommentEventMapper commentEventMapper) {
        return new CommentEventPublisher(redisTemplate, commentTopic, objectMapper, commentEventMapper);
    }
}

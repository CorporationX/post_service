package faang.school.postservice.config;

import faang.school.postservice.topic.CommentEventTopic;
import faang.school.postservice.topic.LikeEventTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisPubSubConfig {

    @Value("${spring.data.redis.topics.comment_event_topic}")
    private String commentEventTopic;

    @Value("${spring.data.redis.topics.like_event_topic}")
    private String likeEventTopic;

    @Bean
    CommentEventTopic commentEventTopic() {
        return new CommentEventTopic(commentEventTopic);
    }

    @Bean
    LikeEventTopic likeEventTopic() {
        return new LikeEventTopic(likeEventTopic);
    }

    @Bean
    RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }
}

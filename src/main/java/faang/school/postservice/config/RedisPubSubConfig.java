package faang.school.postservice.config;

import faang.school.postservice.topic.CommentEventTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisPubSubConfig {
    @Value("${spring.data.redis.topics.comment_event_topic}")
    private String topic;

    @Bean
    CommentEventTopic commentEventTopic() {
        return new CommentEventTopic(topic);
    }

    @Bean
    RedisTemplate<String, String> redisTemplate() {
        return new RedisTemplate<>();
    }
}

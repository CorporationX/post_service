package faang.school.postservice.config;

import faang.school.postservice.topic.CommentEventTopic;
import faang.school.postservice.topic.PostTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisPubSubConfig {
    @Value("${spring.data.redis.channels.comment-event-topic}")
    private String commentEventTopic;

    @Value("${spring.data.redis.channels.post-channel}")
    private String postTopic;

    @Bean
    CommentEventTopic commentEventTopic() {
        return new CommentEventTopic(commentEventTopic);
    }

    @Bean
    PostTopic postTopic() {
        return new PostTopic(postTopic);
    }

    @Bean
    RedisTemplate<String, String> redisTemplate() {
        return new RedisTemplate<>();
    }
}

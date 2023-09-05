package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.CommentEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisherForAnalytics extends AbstractPublisher<CommentEventDto> {
    public CommentEventPublisherForAnalytics(RedisTemplate<String, Object> redisTemplate, ObjectMapper jsonMapper,
                                             @Value("${spring.data.redis.channels.comment_event.name}") String topic) {
        super(redisTemplate, jsonMapper, topic);
    }
}

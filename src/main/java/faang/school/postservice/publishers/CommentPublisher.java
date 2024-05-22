package faang.school.postservice.publishers;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentPublisher extends AbstractPublisher<CommentEvent> {
    public CommentPublisher(RedisTemplate<String, Object> redisTemplate,
                            ObjectMapper jsonMapper,
                            @Value("${spring.data.redis.channels.comment_channel.name}") String commentChannelTopic) {
        super(redisTemplate, jsonMapper, commentChannelTopic);
    }
}
package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisher extends MessagePublisher<CommentEventDto> {

    public CommentEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                 ObjectMapper jsonMapper,
                                 @Value("${spring.data.redis.channels.comment_channel.name}") String commentTopic) {
        super(redisTemplate, jsonMapper, commentTopic);

    }
}

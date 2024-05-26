package faang.school.postservice.publishers;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisher extends AbstractPublisher<CommentEventDto> {

    public CommentEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                 ObjectMapper jsonMapper,
                                 @Value("${spring.data.redis.channels.comment.name}") String commentTopic) {
        super(redisTemplate, jsonMapper, commentTopic);

    }
}

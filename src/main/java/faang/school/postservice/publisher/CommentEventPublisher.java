package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommentEventPublisher extends AbstractEventPublisher<CommentEventDto> {

    @Value("${spring.data.redis.channels.comment_channel.name}")
    private String commentTopic;

    public CommentEventPublisher(RedisTemplate<String,Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    public void publish(CommentEventDto commentEvent) {
        publish(commentEvent, commentTopic);
    }
}
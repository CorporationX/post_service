package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentEventDto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisher extends AbstractEventPublisher<CommentEventDto> {
    @Value("${spring.data.redis.channels.comment_channel.name}")
    private String commentTopic;

    public CommentEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper mapper) {
        super(redisTemplate, mapper);
    }

    public void publish(CommentEventDto commentEventDto) {
        publish(commentEventDto, commentTopic);
    }

}

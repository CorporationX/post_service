package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.MessagePublisher;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.mapper.CommentEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentEventPublisher implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic commentTopic;
    private final ObjectMapper objectMapper;
    private final CommentEventMapper commentEventMapper;

    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend(commentTopic.getTopic(), message);
    }

    @Async
    public void createCommentEvent(CommentDto result) {
        try {
            CommentEvent commentEvent = commentEventMapper.commentDtoToCommentEvent(result);
            publish(objectMapper.writeValueAsString(commentEvent));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}

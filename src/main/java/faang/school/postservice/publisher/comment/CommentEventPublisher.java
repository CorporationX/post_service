package faang.school.postservice.publisher.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Qualifier(value = "commentTopic")
    private final ChannelTopic topic;

    public void publish(CommentEvent event) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(event);
        redisTemplate.convertAndSend(topic.getTopic(), json);
        log.info("Message published. Comment (id {}) has been created by user (id {}) for post (id {}). "
                , event.commentId(), event.authorId(), event.postId());
    }
}
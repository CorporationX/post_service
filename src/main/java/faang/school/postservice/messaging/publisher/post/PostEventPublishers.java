package faang.school.postservice.messaging.publisher.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.post.PostEvent;
import faang.school.postservice.exception.ExceptionMessages;
import faang.school.postservice.messaging.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostEventPublishers implements EventPublisher<PostEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic postTopic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(PostEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            log.info("Publishing event: {}", event);
            redisTemplate.convertAndSend(postTopic.getTopic(), message);
        } catch (JsonProcessingException e) {
            log.error(ExceptionMessages.SERIALIZATION_ERROR + event, e);
        } catch (Exception e) {
            log.error(ExceptionMessages.UNEXPECTED_ERROR + e.getMessage());
        }
    }
}
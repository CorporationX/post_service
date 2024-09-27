package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.CommentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommentEventPublisher implements MessagePublisher<CommentEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;
    private final ObjectMapper objectMapper;

    public CommentEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                 @Qualifier("commentTopic") ChannelTopic channelTopic, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.channelTopic = channelTopic;
        this.objectMapper = objectMapper;
    }
    @Override
    public void publish(CommentEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channelTopic.getTopic(), message);
            log.info("Successfully published CommentEvent to Redis: {}", message);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize CommentEvent: {}", event, e);
            throw new RuntimeException("Error serializing CommentEvent", e);
        }
    }
}

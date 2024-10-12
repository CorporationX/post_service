package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.topic.CommentEventTopic;
import faang.school.postservice.topic.PostTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostPublisher implements MessagePublisher {
    private final RedisTemplate<String, String> redisTemplate;
    private final PostTopic topic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(Object message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            redisTemplate.convertAndSend(topic.getTopic(), json);
            log.info("Post event sent: {}", message);
        } catch (JsonProcessingException e) {
            log.error("not converted to json: {}", message, e);
        }
    }
}

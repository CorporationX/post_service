package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChannelTopic channelTopic;

    public void publish(T event) {
        try {
            redisTemplate.convertAndSend(channelTopic.getTopic(), objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            String msg = "Exception  while sirializing json: " + e.getMessage();
            log.error(msg);
            throw new RuntimeException(msg);
        }
    }
}

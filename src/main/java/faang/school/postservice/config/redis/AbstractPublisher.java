package faang.school.postservice.config.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.annotation.Async;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractPublisher<T> implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic nameTopic;
    private final ObjectMapper objectMapper;

    @Autowired
    private T eventMapper;

    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend(nameTopic.getTopic(), message);
    }

    @Async
    public void createEvent(Object dto) {
        try {
            Object event = mapToEvent(dto);
            publish(objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    protected abstract Object mapToEvent(Object dto);
}

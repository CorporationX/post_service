package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractRedisPublisher<T> implements MessagePublisher<T> {
    private final ChannelTopic channelTopic;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(T message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            redisTemplate.convertAndSend(channelTopic.getTopic(), json);
            log.info("notification {} was published at channel {}", json, channelTopic);
        } catch (JsonProcessingException e) {
            log.error("failed published notification at channel {}", channelTopic);
        }
    }
}

package faang.school.postservice.service.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostEventPublisher implements MessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic postTopic;
    private final ObjectMapperWriter writer;

    @Override
    public void publish(Object message) {
        log.info("PostEventPublisher is sending message to Redis");
        String jsonString = writer.asJsonString(message);
        redisTemplate.convertAndSend(postTopic.getTopic(), jsonString);
    }
}

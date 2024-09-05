package faang.school.postservice.service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
public abstract class MessagePublisher {
    private final RedisTemplate<String, Object> template;
    private final String topicName;

    public void publish(String event) {
        template.convertAndSend(topicName, event);
    }
}

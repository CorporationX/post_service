package faang.school.postservice.publishers;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.LikeEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikeEventPublisher extends AbstractPublisher<LikeEvent> {

    public LikeEventPublisher(RedisTemplate<String, Object> redisTemplate,
                              ObjectMapper objectMapper,
                              String channelName) {
        super(redisTemplate, objectMapper, channelName);
    }
}
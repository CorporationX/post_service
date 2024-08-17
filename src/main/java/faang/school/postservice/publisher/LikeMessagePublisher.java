package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.LikeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LikeMessagePublisher extends GenericMessagePublisher<LikeEvent> {

    public LikeMessagePublisher(RedisTemplate<String, Object> redisTemplate,
                                ChannelTopic likeTopic,
                                ObjectMapper objectMapper) {
        super(redisTemplate, likeTopic, objectMapper);
    }
}

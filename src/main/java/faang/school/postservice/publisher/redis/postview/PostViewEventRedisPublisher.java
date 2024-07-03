package faang.school.postservice.publisher.redis.postview;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.redis.event.PostViewEventDto;
import faang.school.postservice.publisher.redis.AbstractEventRedisPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class PostViewEventRedisPublisher extends AbstractEventRedisPublisher<PostViewEventDto> {

    public PostViewEventRedisPublisher(RedisTemplate<String, Object> redisTemplate,
                                       ChannelTopic postViewTopic,
                                       ObjectMapper objectMapper) {
        super(redisTemplate, postViewTopic, objectMapper);
    }
}

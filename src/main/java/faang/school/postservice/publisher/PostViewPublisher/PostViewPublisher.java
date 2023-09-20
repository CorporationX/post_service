package faang.school.postservice.publisher.PostViewPublisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostViewEventDto;
import faang.school.postservice.publisher.AbstractRedisPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class PostViewPublisher extends AbstractRedisPublisher<PostViewEventDto> {
    public PostViewPublisher(@Qualifier("viewProfileTopic") ChannelTopic channelTopic,
                             RedisTemplate<String, Object> redisTemplate,
                             ObjectMapper objectMapper) {
        super(channelTopic, redisTemplate, objectMapper);
    }
}

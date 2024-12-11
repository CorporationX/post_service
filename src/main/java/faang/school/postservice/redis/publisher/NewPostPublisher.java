package faang.school.postservice.redis.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.dto.PostDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class NewPostPublisher extends AbstractEventPublisher<PostDto> {
    public NewPostPublisher(@Qualifier("eventRedisTemplate") RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper,
                            @Qualifier("hashtagTopic") ChannelTopic channelTopic) {
        super(redisTemplate, objectMapper, channelTopic);
    }
}

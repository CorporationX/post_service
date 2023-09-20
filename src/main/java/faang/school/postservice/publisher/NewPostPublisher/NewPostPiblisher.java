package faang.school.postservice.publisher.NewPostPublisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.publisher.AbstractRedisPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class NewPostPiblisher extends AbstractRedisPublisher<PostDto> {
    public NewPostPiblisher(@Qualifier("hashtagTopic") ChannelTopic channelTopic,
                            RedisTemplate<String, Object> redisTemplate,
                            ObjectMapper objectMapper) {
        super(channelTopic, redisTemplate, objectMapper);
    }
}

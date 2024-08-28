package faang.school.postservice.publisher;

import faang.school.postservice.dto.like.LikeEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class PostLikeEventPublisher extends RedisMessagePublisher<LikeEvent> {

    public PostLikeEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                     ChannelTopic postLikeTopic) {
        super(redisTemplate, postLikeTopic);
    }
}

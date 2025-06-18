package faang.school.postservice.service.publisher.post;

import faang.school.postservice.dto.event.PostViewEventDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service("redisPostViewEventPublisher")
public class RedisPostViewEventPublisher implements PostViewEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic postViewTopic;

    public RedisPostViewEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            @Qualifier("postViewTopic") ChannelTopic postViewTopic) {
        this.redisTemplate = redisTemplate;
        this.postViewTopic = postViewTopic;
    }

    @Override
    public void publish(PostViewEventDto event) {
        redisTemplate.convertAndSend(postViewTopic.getTopic(), event);
    }
}

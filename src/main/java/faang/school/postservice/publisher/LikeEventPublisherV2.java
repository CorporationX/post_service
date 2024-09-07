package faang.school.postservice.publisher;

import faang.school.postservice.event.LikeEventV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class LikeEventPublisherV2 implements MessagePublisherV2<LikeEventV2> {
    private final RedisTemplate<String, Object> template;
    @Value("${spring.data.redis.channels.like_post_channel.name}")
    private ChannelTopic topic;

    public LikeEventPublisherV2(RedisTemplate<String, Object> template) {
        this.template = template;
    }

    @Override
    public void publish(LikeEventV2 likeEvent) {
        template.convertAndSend(topic.getTopic(), likeEvent);
    }
}

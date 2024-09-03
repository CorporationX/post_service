package faang.school.postservice.publisher;

import faang.school.postservice.config.RedisConfigV2;
import faang.school.postservice.event.LikeEventV2;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventPublisherV2 implements MessagePublisherV2<LikeEventV2> {
    private final RedisTemplate<String, Object> template;
    private final ChannelTopic topic;

    @Override
    public void publish(LikeEventV2 likeEvent) {
        template.convertAndSend(topic.getTopic(), likeEvent);
    }
}

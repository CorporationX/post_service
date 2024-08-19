package faang.school.postservice.publisher;

import faang.school.postservice.event.LikeEventV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
//@RequiredArgsConstructor
public class LikeEventPublisherV2 implements MessagePublisherV2 {
    @Autowired
    private RedisTemplate<String, Object> template;
    @Autowired
    private ChannelTopic topic;

//    @Autowired
    public LikeEventPublisherV2(RedisTemplate<String, Object> template,
                                ChannelTopic topic) {
        this.template = template;
        this.topic = topic;
    }

    public void publish(LikeEventV2 likeEvent) {
        template.convertAndSend(topic.getTopic(), likeEvent);
    }
}

package faang.school.postservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostViewEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic postViewTopic;

    public void publish(PostViewEvent postViewEvent) {
        redisTemplate.convertAndSend(postViewTopic.getTopic(), postViewEvent);
        log.info("Post view event published: {}", postViewEvent);
    }
}

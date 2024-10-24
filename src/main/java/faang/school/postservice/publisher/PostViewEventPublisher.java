package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.PostViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostViewEventPublisher implements MessagePublisher<PostViewEvent> {

    private final RedisTemplate<String, PostViewEvent> redisTemplate;

    @Qualifier("postViewChannel")
    private final ChannelTopic postViewTopic;

    @Override
    public void publish(PostViewEvent event) {
        try {
            redisTemplate.convertAndSend(postViewTopic.getTopic(), event);
            log.debug("Publish post view event: {}", event);
        } catch (Exception e) {
            log.error("Failed to publish post view event to {}", postViewTopic.getTopic(), e);
            throw new RuntimeException(e);
        }
    }
}

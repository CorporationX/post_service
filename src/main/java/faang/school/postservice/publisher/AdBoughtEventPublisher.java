package faang.school.postservice.publisher;

import faang.school.postservice.model.event.AdBoughtEvent;
import faang.school.postservice.model.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdBoughtEventPublisher {

    private final ChannelTopic adBoughtEventTopic;
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(AdBoughtEvent adBoughtEvent) {
        redisTemplate.convertAndSend(adBoughtEventTopic.getTopic(), adBoughtEvent);
        log.info("Ad bought event was sent");
    }
}

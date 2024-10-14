package faang.school.postservice.service.messaging.likepost;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.service.messaging.AbstractEventPublisher;
import faang.school.postservice.service.messaging.MessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LikeEventPublisher extends AbstractEventPublisher<LikePostEvent> implements MessagePublisher<LikePostEvent> {

    private final ChannelTopic likeTopic;

    public LikeEventPublisher(RedisTemplate<String, Object> redisTemplate,
                              ObjectMapper objectMapper,
                              ChannelTopic likeTopic) {
        super(redisTemplate, objectMapper);
        this.likeTopic = likeTopic;
    }

    @Override
    public void publish(LikePostEvent likePostEvent) {
        log.info("Publishing LikeEvent: {}", likePostEvent);
        String eventJson = eventJson(likePostEvent);
        redisTemplate.convertAndSend(likeTopic.getTopic(), eventJson);
    }
}

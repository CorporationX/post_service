package faang.school.postservice.publisher.like;

import faang.school.postservice.dto.event.LikePostEvent;
import faang.school.postservice.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikePostEventPublisher implements MessagePublisher<LikePostEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic likePostChannel;

    @Override
    public void publish(LikePostEvent message) {
        redisTemplate.convertAndSend(likePostChannel.getTopic(), message);
        log.info("Message was send {}, in topic - {}", message, likePostChannel.getTopic());
    }
}

package faang.school.postservice.publisher.like;

import faang.school.postservice.dto.like.LikeEventDto;
import faang.school.postservice.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LikeEventPublisher implements MessagePublisher<LikeEventDto> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic likeEventsTopic;

    @Override
    public void publish(LikeEventDto likeEventDto) {
        redisTemplate.convertAndSend(likeEventsTopic.getTopic(), likeEventDto);
        log.debug("Like event dto sent {}, in topic - {}", likeEventDto, likeEventsTopic.getTopic());
    }
}

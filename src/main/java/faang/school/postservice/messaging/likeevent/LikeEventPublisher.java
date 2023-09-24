package faang.school.postservice.messaging.likeevent;

import faang.school.postservice.dto.like.LikeEventDto;
import faang.school.postservice.messaging.EventPublisher;
import faang.school.postservice.util.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LikeEventPublisher implements EventPublisher<LikeEventDto> {
    private final RedisTemplate<String, Object> redisTemplate;

    private final ChannelTopic likeTopic;
    private final JsonMapper jsonMapper;

    @Override
    public void publish(LikeEventDto likeEventDto) {
        jsonMapper.toObject(likeEventDto)
                .ifPresent(str -> redisTemplate.convertAndSend(likeTopic.getTopic(), str));
        log.info(likeEventDto + "was send");
    }
}

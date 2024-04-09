package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.LikeEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LikeEventPublisher extends AbstractEventPublisher<LikeEventDto> {

    @Value("${spring.data.redis.channels.like_event_channel.name}")
    private String likePostTopic;

    public LikeEventPublisher(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        super(objectMapper, redisTemplate);
    }

    public void publish(LikeEventDto likeEventDto) {
        convert(likeEventDto, likePostTopic);
    }
}
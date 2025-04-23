package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.LikeEvent;
import faang.school.postservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.like-event}")
    private String likeEventTopic;

    public void publish(LikeEvent event) {
        if (event == null) {
            throw new DataValidationException("Нельзя послать пустое событие");
        }
        redisTemplate.convertAndSend(likeEventTopic, event);
        log.info("Событие о лайке с id {} отправлено", event.getLikeId());
    }
}

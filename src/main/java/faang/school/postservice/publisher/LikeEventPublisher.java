package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.PostLikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeEventPublisher implements MessagePublisher<PostLikeEvent> {

    private final ObjectMapper objectMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.like_topic}")
    private String topicName;

    @Override
    @SneakyThrows
    public void publish(PostLikeEvent event) {
        log.info("Sending {} to redis", event);
        redisTemplate.convertAndSend(topicName, objectMapper.writeValueAsString(event));
    }
}

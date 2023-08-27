package faang.school.postservice.publisher;

import faang.school.postservice.dto.CommentEventDto;
import faang.school.postservice.mapper.JsonObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Data
@RequiredArgsConstructor
@Slf4j
public class CommentEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.comment_event_channel.name}")
    private String commentEventTopicName;

    public void publish(CommentEventDto event) {
        String json = objectMapper.toJson(event);
        redisTemplate.convertAndSend(commentEventTopicName, json);
    }
}
package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.UserIdDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class RedisMessagePublisher implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(Long authorId) {
        UserIdDto user = new UserIdDto(authorId);
        try {
            String json = objectMapper.writeValueAsString(user);
            redisTemplate.convertAndSend(topic.getTopic(), json);
        } catch (JsonProcessingException e) {
            log.error("Ошибка при сериализации в json");
            throw new RuntimeException(e);
        }
    }
}

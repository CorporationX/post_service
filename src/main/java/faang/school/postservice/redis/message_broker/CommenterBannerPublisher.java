package faang.school.postservice.redis.message_broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommenterBannerPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(@NotBlank List<Long> userIdsForBan) throws JsonProcessingException {
        for (long userId : userIdsForBan) {
            String json = objectMapper.writeValueAsString(userId);
            redisTemplate.convertAndSend("commenter_banner_topic", json);
        }
    }
}

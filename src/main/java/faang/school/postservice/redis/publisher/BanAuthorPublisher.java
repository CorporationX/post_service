package faang.school.postservice.redis.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.redis.publisher.dto.AuthorBanDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class BanAuthorPublisher implements MessagePublisher {
    private final StringRedisTemplate redisTemplate;
    private final RedisProperties properties;
    private final ObjectMapper mapper;

    @Override
    public void publish(AuthorBanDto dto) {
        if (Objects.isNull(dto) || Objects.isNull(dto.userId())) {
            throw new IllegalStateException("Can't publish author for ban action: message cannot be null or empty");
        }
        try {
            String data = mapper.writeValueAsString(dto);
            redisTemplate.convertAndSend(properties.getUserBanChannelName(), data);
        } catch(JsonProcessingException e) {
            throw new IllegalStateException("Faced issues during serialization of AuthorBanDto");
        }
    }
}

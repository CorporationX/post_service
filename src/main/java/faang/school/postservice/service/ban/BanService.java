package faang.school.postservice.service.ban;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.BanEvent;
import faang.school.postservice.redis.RedisMessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BanService {

    @Value("${banned.value}")
    private int valueBanned;
    private final RedisMessagePublisher redisMessagePublisher;
    private final ObjectMapper objectMapper;

    public <T> void checkAndBannedUser(Map<Long, List<T>> authorWithoutVerification) {
        authorWithoutVerification.forEach((authorId, items) -> {
            if (items.size() > valueBanned) {
                try {
                    BanEvent banEvent = new BanEvent();
                    banEvent.setAuthorId(authorId);
                    redisMessagePublisher.publish(objectMapper.writeValueAsString(banEvent));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
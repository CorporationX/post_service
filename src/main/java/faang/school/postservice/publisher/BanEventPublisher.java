package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.publisher.events.BanEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class BanEventPublisher {

    @Value("${spring.data.redis.channels.user_ban_channel.name}")
    private String userBanChannelName;

    private final ObjectMapper objectMapper;
    private final RedisMessagePublisher redisMessagePublisher;

    public void publishBanEvent(Long userId) {
        BanEvent banEvent = new BanEvent();

        banEvent.setUserId(userId);

        try {
            String json = objectMapper.writeValueAsString(banEvent);

            redisMessagePublisher.publish(userBanChannelName, json);

            log.info("User ID to ban is published");
        } catch (JsonProcessingException e) {
            log.error("Failed to convert UserEvent to JSON: {}", e.getMessage());
        }
    }

}

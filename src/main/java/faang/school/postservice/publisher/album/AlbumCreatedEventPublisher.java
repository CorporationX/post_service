package faang.school.postservice.publisher.album;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.AlbumCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlbumCreatedEventPublisher {

    @Value("${spring.data.redis.channels.album_created_channel.name}")
    private String albumCreatedTopic;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(AlbumCreatedEvent albumCreatedEvent) {
        try {
            String json = objectMapper.writeValueAsString(albumCreatedEvent);
            log.info("Publishing album creating event: {}", json);
            redisTemplate.convertAndSend(albumCreatedTopic, json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize album creating event: {}", albumCreatedEvent, e);
            throw new RuntimeException("Failed to serialize album creating event: " + albumCreatedEvent);
        }
    }

}

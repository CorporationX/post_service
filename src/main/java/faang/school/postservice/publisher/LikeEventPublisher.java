package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.LikeEventDto;
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
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.like_event_channel.name}")
    private String likePostTopic;

    public void publish(LikeEventDto likeEventDto) {
        try {
            String json = objectMapper.writeValueAsString(likeEventDto);
            redisTemplate.convertAndSend(likePostTopic, json);
            log.info("Отправлено событие лайка поста с ID: {}, автору поста с ID: {}, от пользователя с ID: {}",
                    likeEventDto.getPostId(), likeEventDto.getAuthorId(), likeEventDto.getUserId());
        } catch (JsonProcessingException e) {
            log.error("Ошибка в обработке", e);
            throw new RuntimeException(e);
        }
    }
}
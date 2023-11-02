package faang.school.postservice.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.redis.LikeEventDto;
import faang.school.postservice.mapper.redis.LikeEventMapper;
import faang.school.postservice.model.Like;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeEventPublisher implements MessagePublisher {

    private final ObjectMapper objectMapper;
    private final LikeEventMapper likeEventMapper;
    private final ChannelTopic likeTopic;
    private final RedisTemplate<String, Object> redisTemplate;


    public void publish(Object object) {
        LikeEventDto likeEventDto = likeEventMapper.toDto((Like) object);
        try {
            String likeEvent = objectMapper.writeValueAsString(likeEventDto);
            redisTemplate.convertAndSend(likeTopic.getTopic(), likeEvent);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException", e);
        }
    }
}

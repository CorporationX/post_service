package faang.school.postservice.publisher.like;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import faang.school.postservice.dto.like.LikeEventDto;
import faang.school.postservice.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventPublisher implements MessagePublisher<LikeEventDto> {

    private final RedisTemplate<String, LikeEventDto> redisTemplate;
    private final ChannelTopic topic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(LikeEventDto likeEventDto) {
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        try {
            String message = objectMapper.writeValueAsString(likeEventDto);
            redisTemplate.convertAndSend(topic.getTopic(), message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.LikeEvent;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class LikeEventPublisher implements MessagePublisher {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ChannelTopic likeEventChannel;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void publish(LikeEvent likeEvent) {
        String jsonProfileViewEvent;
        try {
            jsonProfileViewEvent = objectMapper.writeValueAsString(likeEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(likeEventChannel.getTopic(), jsonProfileViewEvent);
    }
}

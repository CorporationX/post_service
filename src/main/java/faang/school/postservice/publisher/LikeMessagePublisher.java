package faang.school.postservice.publisher;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.FollowerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeMessagePublisher implements MessagePublisher<FollowerEvent>{
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;
    private final ObjectMapper objectMapper;


    @Override
    public void publish(FollowerEvent followerEvent) {
        String message;
        try {
            message = objectMapper.writeValueAsString(followerEvent);
        } catch (JsonProcessingException e) {
            log.info("Method: publish {}", e.getMessage());
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(topic.getTopic(), message);
        System.out.println("Published message: " + message + " to channel: " + topic.getTopic());
    }
}

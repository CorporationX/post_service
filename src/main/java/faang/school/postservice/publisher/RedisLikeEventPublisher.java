package faang.school.postservice.publisher;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.LikeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class RedisLikeEventPublisher implements LikeEventPublisher {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ChannelTopic likeEventTopic;

    private ObjectMapper objectMapper;

    public RedisLikeEventPublisher() {
    }

    public RedisLikeEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.likeEventTopic = topic;
    }

    public void publish(LikeEvent likeEvent) throws JsonProcessingException {
        String jsonLikeEvent = objectMapper.writeValueAsString(likeEvent);
        redisTemplate.convertAndSend(likeEventTopic.getTopic(), jsonLikeEvent);
    }
}

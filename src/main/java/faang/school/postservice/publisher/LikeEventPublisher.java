package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.LikeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikeEventPublisher extends AbstractEventPublisher<LikeEvent> {
    private final String likeChannelName;

    @Autowired
    public LikeEventPublisher(RedisTemplate<String, Object> redisTemplate,
                              ObjectMapper objectMapper,
                              @Value("${spring.data.redis.channels.like_channel}") String likeChannelName) {
        super(redisTemplate, objectMapper);
        this.likeChannelName = likeChannelName;
    }

    @Override
    public void publish(LikeEvent likeEvent) {
        send(likeEvent, likeChannelName);
    }
}

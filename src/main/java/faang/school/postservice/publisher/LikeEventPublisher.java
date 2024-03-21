package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LikeEventPublisher extends AbstractEventPublisher<LikeEvent>{
    private final LikeMapper likeMapper;

    @Autowired
    public LikeEventPublisher(RedisTemplate<String, Object> redisTemplate,
                              ObjectMapper objectMapper,
                              @Value("${spring.data.redis.channels.like_channel}") String likeChannelName,
                              LikeMapper likeMapper) {
        super(redisTemplate, objectMapper, likeChannelName);
        this.likeMapper = likeMapper;
    }

    public void publish(Like like) {
        LikeEvent event = likeMapper.toEventDto(like);
        event.setCreatedAt(LocalDateTime.now());
        publishInTopic(event);
    }
}

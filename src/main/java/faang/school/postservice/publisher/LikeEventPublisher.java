package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeEventDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class LikeEventPublisher extends AbstractEventPublisher<LikeEventDto>{
    public final ChannelTopic likeTopic;

    @Autowired
    public LikeEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, ChannelTopic likeTopic) {
        super(redisTemplate, objectMapper);
        this.likeTopic = likeTopic;
    }

    public void publish(LikeEventDto like) {
        publishInTopic(likeTopic, like);
    }
}

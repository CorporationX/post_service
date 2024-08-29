package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.LikeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LikeMessagePublisher extends AbstractEventPublisher<LikeEvent> {

    private final ChannelTopic likeTopic;

    public LikeMessagePublisher(ObjectMapper objectMapper,
                                RedisTemplate<String, Object> redisTemplate,
                                ChannelTopic likeTopic) {
        super(objectMapper, redisTemplate);
        this.likeTopic = likeTopic;
    }

    @Override
    protected String getChannelTopic() {
        return likeTopic.getTopic();
    }
}

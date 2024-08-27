package faang.school.postservice.messaging.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

public class TestEventPublisher extends AbstractEventPublisher<Object>{

    public TestEventPublisher(ObjectMapper objectMapper,
                              RedisTemplate<String, Object> redisTemplate,
                              ChannelTopic channelTopic) {
        super(objectMapper, redisTemplate, channelTopic);
    }

}

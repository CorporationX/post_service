package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.RateDecreaseEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class RateDecreaseEventPublisher extends AbstractEventPublisher<RateDecreaseEvent> {

    public RateDecreaseEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper,
                              @Qualifier("rateDecreaseTopic") ChannelTopic channelTopic) {
        super(redisTemplate, objectMapper, channelTopic);
    }
}

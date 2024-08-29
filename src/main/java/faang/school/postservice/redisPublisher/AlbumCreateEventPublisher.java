package faang.school.postservice.redisPublisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.AlbumCreatedEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class AlbumCreateEventPublisher extends EventPublisher<AlbumCreatedEvent> {
    public AlbumCreateEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                     ObjectMapper objectMapper,
                                     @Qualifier("albumChannelTopic") ChannelTopic channelTopic) {
        super(redisTemplate, objectMapper, channelTopic);
    }
}

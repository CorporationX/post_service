package faang.school.postservice.redisPublisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEvent;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;


@Component
public class CommentEventPublisher extends EventPublisher<CommentEvent> {

    public CommentEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                 ObjectMapper objectMapper,
                                 @Qualifier("commentEventTopic") ChannelTopic channelTopic) {
        super(redisTemplate, objectMapper, channelTopic);
    }

//    public CommentEventPublisher(RedisTemplate<String, Object> redisTemplate,
//                                 ObjectMapper objectMapper,
//                                 @Qualifier("commentAchievementTopic") ChannelTopic channelTopic) {
//        super(redisTemplate, objectMapper, channelTopic);
//    }
}

package faang.school.postservice.publisher.commentPublisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.comment.NewCommentEvent;
import faang.school.postservice.publisher.AbstractRedisPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class CommentRedisPublisher extends AbstractRedisPublisher<NewCommentEvent> {
    public CommentRedisPublisher(@Qualifier("commentTopic") ChannelTopic channelTopic,
                                 RedisTemplate<String, Object> redisTemplate,
                                 ObjectMapper objectMapper
    ) {
        super(channelTopic, redisTemplate, objectMapper);
    }
}
package faang.school.postservice.messaging.publisher.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.comment.CommentEvent;
import faang.school.postservice.messaging.publisher.AbstractEventPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisher extends AbstractEventPublisher<CommentEvent> {

    public CommentEventPublisher(ObjectMapper objectMapper, RedisTemplate redisTemplate,
                                 @Qualifier(value = "commentChannel") ChannelTopic channelTopic) {
        super(objectMapper, redisTemplate, channelTopic);
    }

    @Override
    public void publish(CommentEvent event) {
        super.publish(event);
    }
}

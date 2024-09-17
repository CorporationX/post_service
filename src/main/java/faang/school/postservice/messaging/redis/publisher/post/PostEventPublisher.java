package faang.school.postservice.messaging.redis.publisher.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.post.PostEvent;
import faang.school.postservice.messaging.redis.publisher.comment.AbstractEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PostEventPublisher extends AbstractEventPublisher<PostEvent> {

    public PostEventPublisher(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate,
                              @Qualifier("postTopic") ChannelTopic channelTopic) {
        super(objectMapper, redisTemplate, channelTopic);
    }

    @Override
    public void publish(PostEvent event) {
        super.publish(event);
    }
}
package faang.school.postservice.messaging.publisher.redis.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.redis.post.PostEvent;
import faang.school.postservice.messaging.publisher.redis.comment.AbstractEventPublisher;
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
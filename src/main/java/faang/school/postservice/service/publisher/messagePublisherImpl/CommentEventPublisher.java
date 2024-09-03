package faang.school.postservice.service.publisher.messagePublisherImpl;

import faang.school.postservice.service.publisher.MessagePublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisher extends MessagePublisher {

    public CommentEventPublisher(RedisTemplate<String, Object> template,
                                 @Value("${spring.data.redis.channels.comment}") String topicName) {
        super(template, topicName);
    }
}

package faang.school.postservice.publisher.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.publisher.AbstractEventPublisher;
import faang.school.postservice.publisher.MessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostEventPublisher extends AbstractEventPublisher implements MessagePublisher {
    @Autowired
    public PostEventPublisher(ObjectMapper objectMapper,
                              RedisTemplate<String, Object> redisTemplate,
                              ChannelTopic postCreateTopic) {
        super(objectMapper, redisTemplate, postCreateTopic);
    }

    @Override
    public void publish(Object object) {
        convertAndSend(object);
    }
}

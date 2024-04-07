package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.CommentEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisher extends AbstractEventPublisher<CommentEventDto> {


    public CommentEventPublisher(ObjectMapper objectMapper,
                                 RedisTemplate<String, Object> redisTemplate,
                                 @Value("${spring.data.redis.channel.comment}") String topic) {
        super(objectMapper, redisTemplate, topic);
    }


    @Override
    public void publishInTopic(CommentEventDto event) {
        super.publishInTopic(event);
    }
}
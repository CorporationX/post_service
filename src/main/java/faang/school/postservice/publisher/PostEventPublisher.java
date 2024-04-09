package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostEventPublisher extends AbstractEventPublisher<PostEventDto> {


    private String topic;

    public PostEventPublisher(ObjectMapper objectMapper,
                              RedisTemplate<String, Object> redisTemplate,
                              @Value("${spring.data.redis.channel.post_channel") String topic) {
        super(objectMapper, redisTemplate, topic);
    }


    public void publish(PostEventDto postEventDto) {
        publishInTopic(postEventDto);
    }
}
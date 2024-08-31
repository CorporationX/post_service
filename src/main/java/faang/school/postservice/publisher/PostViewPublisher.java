package faang.school.postservice.publisher;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostViewPublisher extends AbstractPublisher {
    public PostViewPublisher(
            @Value("${spring.data.redis.channels.post-view}") String topicName,
            RedisTemplate<String,
            Object> redisTemplate) {
        super(topicName, redisTemplate);
    }
}

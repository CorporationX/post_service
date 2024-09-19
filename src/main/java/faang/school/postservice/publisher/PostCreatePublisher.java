package faang.school.postservice.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostCreatePublisher extends AbstractPublisher {
    public PostCreatePublisher(
            @Value("${spring.data.redis.channels.post-create}") String topicName,
            RedisTemplate<String,
                    Object> redisTemplate) {
        super(topicName, redisTemplate);
    }
}

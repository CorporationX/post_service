package faang.school.postservice.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostLikePublisher extends AbstractPublisher {

    public PostLikePublisher(@Value("${spring.data.redis.channels.post-like}") String topicName,
                             RedisTemplate<String, Object> redisTemplate) {
        super(topicName, redisTemplate);
    }
}

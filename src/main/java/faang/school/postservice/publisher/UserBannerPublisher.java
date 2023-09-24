package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserBannerPublisher extends AbstractPublisher<Long> {
    public UserBannerPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper jsonMapper,
                               @Value("${spring.data.redis.channels.user_channels}") String topic) {
        super(redisTemplate, jsonMapper, topic);
    }
}
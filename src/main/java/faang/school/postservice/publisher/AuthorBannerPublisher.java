package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthorBannerPublisher extends AbstractEventPublisher<List<Long>> {
    public AuthorBannerPublisher(RedisTemplate<String, Object> redisTemplate,
                                 ObjectMapper objectMapper,
                                 @Value("${spring.data.redis.channels.user_ban}") String topicChannelName) {
        super(redisTemplate, objectMapper, topicChannelName);
    }

    public void publish(List<Long> authorIds) {
        publishInTopic(authorIds);
    }

}

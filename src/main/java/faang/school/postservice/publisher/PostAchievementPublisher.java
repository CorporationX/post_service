package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.redis.PostAchievementEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostAchievementPublisher extends AbstractEventPublisher<PostAchievementEventDto> {
    public PostAchievementPublisher(RedisTemplate<String, Object> redisTemplate,
                                    ObjectMapper objectMapper,
                                    @Value("${spring.data.redis.channels.post_achievement}") String topicChannelName) {
        super(redisTemplate, objectMapper, topicChannelName);
    }

    public void publish(PostAchievementEventDto event) {
        publishInTopic(event);
    }
}

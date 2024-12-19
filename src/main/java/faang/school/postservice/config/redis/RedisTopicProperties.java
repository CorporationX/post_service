package faang.school.postservice.config.redis;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class RedisTopicProperties {

    @Value("${spring.data.redis.channels.comment}")
    private String postCommentChannel;

    @Value("${spring.data.redis.channels.like-event-topic}")
    private String likeEventTopic;
}

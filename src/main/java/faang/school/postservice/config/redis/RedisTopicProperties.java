package faang.school.postservice.config.redis;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class RedisTopicProperties {

    @Value("${spring.data.redis.channels.ban-user-channel.name}")
    private String banUserTopic;

    @Value("${spring.data.redis.channels.post-like-channel.name}")
    private String postLikeTopic;
}

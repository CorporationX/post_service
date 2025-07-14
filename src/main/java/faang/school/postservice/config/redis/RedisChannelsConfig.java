package faang.school.postservice.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "spring.data.redis.channel")
@Component
@Getter
@Setter
public class RedisChannelsConfig {
    private String likes;
    private String likeReceived;
    private String postCreated;
}

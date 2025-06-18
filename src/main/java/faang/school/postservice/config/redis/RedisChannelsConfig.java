package faang.school.postservice.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "spring.data.redis.channel")
@Getter
@Setter
public class RedisChannelsConfig {
    private String likes;
    private String likesReceived;
}

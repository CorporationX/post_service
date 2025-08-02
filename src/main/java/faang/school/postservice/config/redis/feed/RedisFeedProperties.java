package faang.school.postservice.config.redis.feed;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties("spring.data.redis-news-feed")
public class RedisFeedProperties {
    private String host;
    private int port;
}

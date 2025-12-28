package faang.school.postservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.feed.redis")
public class FeedRedisProperties {
    private int maxSize = 500;
    private String keyPrefix = "feed:v1:";
}
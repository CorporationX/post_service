package faang.school.postservice.config.redis.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "cache.feed")
public record FeedCacheProperties(
        String name,
        int batch,
        Duration timeToLive
) {
}

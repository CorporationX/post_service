package faang.school.postservice.config.properties.cache.feed;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "feed")
public record FeedProperties(
        int pageSize
) {
}

package faang.school.postservice.config.properties.cache.author;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "feed.cache.author")
public record AuthorCacheProperties(
        String keyPrefix,
        Duration ttl,
        int lockWaitSeconds,
        int lockLeaseSeconds,
        int ttlJitterSeconds
) {}

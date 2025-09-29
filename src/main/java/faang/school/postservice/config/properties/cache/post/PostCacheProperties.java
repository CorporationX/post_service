package faang.school.postservice.config.properties.cache.post;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

@ConfigurationProperties(prefix = "feed.cache.post")
public record PostCacheProperties(
        @DefaultValue("feed:post:") String keyPrefix,
        @DefaultValue("PT24H") Duration ttl
) {}

package faang.school.postservice.config.properties.cache.feed;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "feed.cache.feed")
public record FeedCacheProperties(
        @Min(1) int maxSize,
        @NotBlank String keyPrefix,
        @Min(0) int ttlSeconds
) {
}

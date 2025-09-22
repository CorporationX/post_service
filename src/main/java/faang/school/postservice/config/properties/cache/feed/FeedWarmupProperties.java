package faang.school.postservice.config.properties.cache.feed;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "feed.cache.warm-up")
public record FeedWarmupProperties(
        @Min(1) int limit,
        @Min(1) int concurrency,
        @Min(1) int partitions,
        @Min(1) int lockTtlHours,
        @Min(1) int batchSize
) {}

package faang.school.postservice.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("spring.data.redis.cache-duration")
public record CacheDuration(
        Duration posts,
        Duration feed,
        Duration authors
) {
}

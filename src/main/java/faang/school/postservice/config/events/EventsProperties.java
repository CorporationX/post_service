package faang.school.postservice.config.events;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "events.redis")
public record EventsProperties(
        Topics topics,
        Retry retry
) {
    public record Topics(String comment) {}
    public record Retry(int maxAttempts, Duration delay) {}
}
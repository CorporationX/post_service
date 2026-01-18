package faang.school.postservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.feed.controller")
public record FeedControllerProperties(int defaultLimit) {
}
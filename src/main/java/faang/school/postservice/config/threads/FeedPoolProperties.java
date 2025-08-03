package faang.school.postservice.config.threads;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("thread.feed")
public record FeedPoolProperties(
        int coreSize,
        int maxSize,
        String prefix
) {
}

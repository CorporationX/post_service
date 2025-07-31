package faang.school.postservice.config.threads;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("thread.newsfeed")
public record NewsfeedPoolProperties(
        int coreSize,
        int maxSize,
        String prefix
) {
}

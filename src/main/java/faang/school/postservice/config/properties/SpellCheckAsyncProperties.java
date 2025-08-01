package faang.school.postservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "post.correction")
public record SpellCheckAsyncProperties(
        int batchSize,
        int coreSize,
        int maxSize,
        int queueCapacity
) {
}

package faang.school.postservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "thread-pool.view-count-updater")
public record ViewCountUpdaterPoolProperties(
        int corePoolSize,
        int maxPoolSize,
        String threadNamePrefix
) {}

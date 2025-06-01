package faang.school.postservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "thread-pool.post-after-publisher")
public record PostAfterPublisherAsyncProperties(
        int corePoolSize,
        int maxPoolSize,
        int keepAliveSeconds,
        boolean allowCoreThreadTimeOut,
        String threadNamePrefix,
        boolean waitForTasksToCompleteOnShutdown,
        int awaitTerminationSeconds
) {}

package faang.school.postservice.config;

import faang.school.postservice.config.properties.PostAfterPublisherAsyncProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class PostAfterPublisherAsyncConfig {

    private final PostAfterPublisherAsyncProperties properties;

    @Bean(name = "postPublisherExecutor")
    public ThreadPoolTaskExecutor postPublisherExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.corePoolSize());
        executor.setMaxPoolSize(properties.maxPoolSize());
        executor.setKeepAliveSeconds(properties.keepAliveSeconds());
        executor.setAllowCoreThreadTimeOut(properties.allowCoreThreadTimeOut());
        executor.setThreadNamePrefix(properties.threadNamePrefix());
        executor.setWaitForTasksToCompleteOnShutdown(properties.waitForTasksToCompleteOnShutdown());
        executor.setAwaitTerminationSeconds(properties.awaitTerminationSeconds());
        executor.initialize();
        return executor;
    }
}

package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AsyncConfig {

    @Value("${app.async.feed-heat.core-pool-size:1}")
    private int corePoolSize;

    @Value("${app.async.feed-heat.max-pool-size:1}")
    private int maxPoolSize;

    @Value("${app.async.feed-heat.queue-capacity:1}")
    private int queueCapacity;

    @Value("${app.async.feed-heat.thread-name-prefix:feed-heat-}")
    private String threadNamePrefix;

    @Bean(name = "feedHeatExecutor")
    public Executor feedHeatExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }
}


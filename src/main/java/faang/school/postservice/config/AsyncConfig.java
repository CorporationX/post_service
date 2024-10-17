package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "commonTaskExecutor")
    public TaskExecutor taskExecutor(
            @Value("${task-executor.core-pool-size}") int corePoolSize,
            @Value("${task-executor.max-pool-size}") int maxPoolSize,
            @Value("${task-executor.queue-capacity}") int queueCapacity,
            @Value("${task-executor.thread-name-prefix}") String threadNamePrefix) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}
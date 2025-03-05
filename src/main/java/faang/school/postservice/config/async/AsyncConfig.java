package faang.school.postservice.config.async;

import faang.school.postservice.config.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
@RequiredArgsConstructor
public class AsyncConfig {
    private final UserContext userContext;

    @Value("${spring.data.task.execution.pool.core-size}")
    private int threadsCount;

    @Value("${spring.data.task.execution.pool.max-size}")
    private int maxSize;

    @Value("${spring.data.task.execution.pool.queue-capacity}")
    private int queueSize;

    @Bean(name = "workerPool")
    public TaskExecutor workerPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("async-worker-thread-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "warm")
    public TaskExecutor warmPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadsCount);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueSize);
        executor.setThreadNamePrefix("CacheWarmer-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("CacheWarmer-");

        executor.setTaskDecorator(runnable -> {
            long currentUserId;
            try {
                currentUserId = userContext.getUserId();
            } catch (NullPointerException e) {
                userContext.setUserId(1L);
                currentUserId = 1L;
            }

            long finalCurrentUserId = currentUserId;
            return () -> {
                try {
                    userContext.setUserId(finalCurrentUserId);
                    runnable.run();
                } finally {
                    userContext.clear();
                }
            };
        });
        executor.initialize();
        return executor;
    }
}
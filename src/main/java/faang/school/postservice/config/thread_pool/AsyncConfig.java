package faang.school.postservice.config.thread_pool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Конфигурация для создания пула потоков
 *
 * @author Linempy
 * @since 06.08.2025
 */

@EnableAsync
@Configuration
public class AsyncConfig {

    @Bean("afterCommitExecutor")
    public ThreadPoolTaskExecutor afterCommitExecutor(
            @Value("${thread-pool.async.generated-hash.core-pool-size}") int corePoolSize,
            @Value("${thread-pool.async.generated-hash.max-pool-size}") int maxPoolSize,
            @Value("${thread-pool.async.generated-hash.queue-capacity}") int queueCapacity) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setQueueCapacity(queueCapacity);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setCorePoolSize(corePoolSize);

        executor.setThreadNamePrefix("After-Commit-Async-");
        executor.initialize();
        return executor;
    }

    @Bean("feedExecutor")
    public ThreadPoolTaskExecutor postgresTaskExecutor(
            @Value("${thread-pool.async.feedBatch.core-pool-size}") int corePoolSize,
            @Value("${thread-pool.async.feedBatch.max-pool-size}") int maxPoolSize,
            @Value("${thread-pool.async.feedBatch.queue-capacity}") int queueCapacity
    ) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("Feed-Async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
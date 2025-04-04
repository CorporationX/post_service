package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EnableAsync
@Configuration
public class AsyncConfig {

    @Value("${app.async.common.core_pool_size}")
    private int commonCorePoolSize;

    @Value("${app.async.common.max_pool_size}")
    private int commonMaxPoolSize;

    @Value("${app.async.common.queue_capacity}")
    private int commonQueueCapacity;

    @Value("${app.async.common.thread_name_prefix:moderation-}")
    private String commonThreadNamePrefix;

    @Value("${app.async.hash_generator.core_pool_size}")
    private int hashCorePoolSize;

    @Value("${app.async.hash_generator.max_pool_size}")
    private int hashMaxPoolSize;

    @Value("${app.async.hash_generator.queue_capacity}")
    private int hashQueueCapacity;

    @Value("${app.async.hash_generator.thread_name_prefix:hash-}")
    private String hashThreadNamePrefix;

    @Bean(name = "commonTaskExecutor")
    public ThreadPoolTaskExecutor moderationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(commonCorePoolSize);
        executor.setMaxPoolSize(commonMaxPoolSize);
        executor.setQueueCapacity(commonQueueCapacity);
        executor.setThreadNamePrefix(commonThreadNamePrefix);
        executor.initialize();
        return executor;
    }

    @Bean(name = "hashGeneratorExecutor")
    public Executor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashCorePoolSize);
        executor.setMaxPoolSize(hashMaxPoolSize);
        executor.setQueueCapacity(hashQueueCapacity);
        executor.setThreadNamePrefix(hashThreadNamePrefix);
        executor.initialize();
        return executor;
    }

    @Bean
    public ExecutorService executorService(
            @Value("${app.async.executor_service.pool_size}") int poolSize) {
        return Executors.newFixedThreadPool(poolSize);
    }
}
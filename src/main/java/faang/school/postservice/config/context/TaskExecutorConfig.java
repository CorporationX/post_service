package faang.school.postservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class TaskExecutorConfig {

    @Value("${spring.task.scheduling.comment.executor.core_pool_size}")
    private int corePoolSize;
    @Value("${spring.task.scheduling.comment.executor.max_poll_size}")
    private int maxPoolSize;
    @Value("${spring.task.scheduling.comment.executor.queue_capacity}")
    private int queueCapacity;
    @Value("${spring.task.scheduling.comment.executor.thread_prefix}")
    private String threadNamePrefix;

    @Bean
    public Executor commentModeratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}

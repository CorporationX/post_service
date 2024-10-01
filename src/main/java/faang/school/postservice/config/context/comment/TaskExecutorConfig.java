package faang.school.postservice.config.context.comment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class TaskExecutorConfig {

    @Value("${comment.verify-task-executor.core-pool-size}")
    private int corePoolSize;

    @Value("${comment.verify-task-executor.max-pool-size}")
    private int maxPoolSize;

    @Value("${comment.verify-task-executor.queue-capacity}")
    private int queueCapacity;

    @Value("${comment.verify-task-executor.thread-name-prefix}")
    private String threadNamePrefix;

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}

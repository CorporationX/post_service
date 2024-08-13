package faang.school.postservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author Evgenii Malkov
 */
@Configuration
public class ThreadPoolExecutorConfig {
    @Value("${spring.task.execution.pool.max-size}")
    private int poolMaxSize;
    @Value("${spring.task.execution.pool.core-size}")
    private int poolCoreSize;
    @Value("${spring.task.execution.pool.queue-capacity}")
    private int queueCapacity;

    @Bean()
    public TaskExecutor schedulerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(poolMaxSize);
        executor.setCorePoolSize(poolCoreSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();
        return executor;
    }
}

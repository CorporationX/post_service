package faang.school.postservice.config.thread;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {
    @Value("${thread-pool.core-size}")
    private int coreSize;

    @Value("${thread-pool.max-size}")
    private int maxSize;

    @Value("${thread-pool.queue-capacity}")
    private int queueCapacity;

    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }
}

package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class TreadPoolConfig {
    @Value("${spring.task.execution2.pool.core-size}")
    private int coreSize;
    @Value("${spring.task.execution2.pool.max-size}")
    private int maxSize;
    @Value("${spring.task.execution2.pool.keep-alive}")
    private int keepAlive;
    @Value("${spring.task.execution2.pool.queue-capacity}")
    private int queueCapacity;
    @Value("${spring.task.execution2.pool.thread-name-prefix}")
    private String threadNamePrefix;

    @Bean(name = "scheduledPostPublisherPool")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setKeepAliveSeconds(keepAlive);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}

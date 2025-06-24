package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

    @Value("${expired-ads.async.pool-size}")
    private int poolSize;

    @Value("${expired-ads.async.max-pool-size}")
    private int maxPoolSize;

    @Value("${expired-ads.async.queue-capacity}")
    private int queueCapacity;

    @Value("${expired-ads.async.thread-name-prefix}")
    private String threadNamePrefix;

    @Bean("adTaskExecutor")
    public ThreadPoolTaskExecutor adTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}

package faang.school.postservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ScheduledExpiredRemoverThreadPoolConfig {

    @Value("${post.ad-remover.scheduler.thread-pool.core-pool-size}")
    private int corePoolSize;
    @Value("${post.ad-remover.scheduler.thread-pool.max-pool-size}")
    private int maxPoolSize;
    @Value("${post.ad-remover.scheduler.thread-pool.queue-capacity}")
    private int queueCapacity;

    @Bean
    public ThreadPoolTaskExecutor scheduledRemoverThreadPoolExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        return executor;
    }
}
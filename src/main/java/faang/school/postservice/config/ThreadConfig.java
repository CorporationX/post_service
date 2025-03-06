package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class ThreadConfig {

    @Value("${moderation.thread-pool-size}")
    private int threadPoolSize;
    @Value("${moderation.thread-pool-max-size}")
    private int threadPoolMaxSize;
    @Value("${moderation.thread-pool-queue-capacity}")
    private int threadPoolQueueCapacity;

    @Bean
    public Executor moderateTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolSize);
        executor.setMaxPoolSize(threadPoolMaxSize);
        executor.setQueueCapacity(threadPoolQueueCapacity);
        executor.setThreadNamePrefix("ModerationThread-");
        executor.initialize();
        return executor;
    }

}

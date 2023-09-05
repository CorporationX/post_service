package faang.school.postservice.config;

import faang.school.postservice.config.scheduled.ScheduledTaskThreadPoolConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AsyncConfig {
    private final ModerationThreadPoolConfig threadPoolConfig;
    private final ScheduledTaskThreadPoolConfig scheduledTaskThreadPoolConfig;

    @Bean
    public Executor moderationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setQueueCapacity(threadPoolConfig.getQueueCapacity());
        executor.setMaxPoolSize(threadPoolConfig.getMaxPoolSize());
        executor.setCorePoolSize(threadPoolConfig.getCorePoolSize());
        executor.setThreadNamePrefix(threadPoolConfig.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }

    @Bean
    public Executor scheduledTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setQueueCapacity(scheduledTaskThreadPoolConfig.getQueueCapacity());
        executor.setMaxPoolSize(scheduledTaskThreadPoolConfig.getMaxPoolSize());
        executor.setCorePoolSize(scheduledTaskThreadPoolConfig.getCorePoolSize());
        executor.initialize();
        return executor;
    }
}

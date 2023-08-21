package faang.school.postservice.config;

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
    private final ThreadPoolConfig threadPoolConfig;

    @Bean(name="taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setQueueCapacity(threadPoolConfig.getQueueCapacity());
        executor.setMaxPoolSize(threadPoolConfig.getMaxPoolSize());
        executor.setCorePoolSize(threadPoolConfig.getCorePoolSize());
        executor.setThreadNamePrefix(threadPoolConfig.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }
}

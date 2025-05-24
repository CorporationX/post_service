package faang.school.postservice.config.async;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

    @Value("${threadpool.core-pool-size}")
    @NotNull(message = "Core pool size must be specified")
    @Min(value = 1, message = "Core pool size must be positive")
    private Integer corePoolSize;

    @Value("${threadpool.max-pool-size}")
    @NotNull(message = "Max pool size must be specified")
    @Min(value = 1, message = "Max pool size must be positive")
    private Integer maxPoolSize;

    @Value("${threadpool.queue-capacity}")
    @NotNull(message = "Queue capacity must be specified")
    @Min(value = 1, message = "Queue capacity must be positive")
    private Integer queueCapacity;

    @Bean(name = "postEventExecutor")
    public ThreadPoolTaskExecutor postEventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("postEventExecutor-");
        executor.initialize();
        return executor;
    }
}
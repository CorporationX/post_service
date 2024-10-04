package faang.school.postservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class PostOperationsAsyncConfig {

    @Value("${post.executor.core-pool-size}")
    private int corePoolSize;

    @Value("${post.executor.max-pool-size}")
    private int maxPoolSize;

    @Value("${post.executor.queue-capacity}")
    private int correcterQueueCapacity;

    @Bean(name = "postOperationsAsyncExecutor")
    public Executor spellCheckAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(correcterQueueCapacity);
        executor.setThreadNamePrefix("PostOperationsAsyncExecutor-");
        executor.initialize();
        return executor;
    }
}

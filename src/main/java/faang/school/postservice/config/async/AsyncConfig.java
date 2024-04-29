package faang.school.postservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;

@Configuration
public class AsyncConfig {

    @Value("${async.corePoolSize}")
    private int corePoolSize;
    @Value("${async.maxPoolSize}")
    private int maxPoolSize;
    @Value("${async.queueCapacity}")
    private int queueCapacity;

    @Bean
    public ExecutorService asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("Async-Executor-");
        executor.initialize();
        return executor.getThreadPoolExecutor();
    }
}
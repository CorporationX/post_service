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
    @Value("${async.feed-heating.core-pool-size}")
    private int feedHeaterCorePoolSize;
    @Value("${async.feed-heating.max-pool-size}")
    private int feedHeaterMaxPoolSize;
    @Value("${async.feed-heating.queue-capacity}")
    private int feedHeaterQueueCapacity;

    @Bean
    public ExecutorService executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("Async-Executor-");
        executor.initialize();
        return executor.getThreadPoolExecutor();
    }

    @Bean("feedHeaterThreadPool")
    public ExecutorService feedHeaterThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(feedHeaterCorePoolSize);
        executor.setMaxPoolSize(feedHeaterMaxPoolSize);
        executor.setQueueCapacity(feedHeaterQueueCapacity);
        executor.initialize();
        return executor.getThreadPoolExecutor();
    }

}
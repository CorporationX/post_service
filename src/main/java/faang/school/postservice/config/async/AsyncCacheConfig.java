package faang.school.postservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncCacheConfig {

    @Value("${async.cache.corePoolSize}")
    private int corePoolSize;

    @Value("${async.cache.maxPoolSize}")
    private int maxPoolSize;

    @Value("${async.cache.queueCapacity}")
    private int queueCapacity;

    @Bean
    public Executor cacheTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("CacheAsyncThread-");
        executor.initialize();
        return executor;
    }
}

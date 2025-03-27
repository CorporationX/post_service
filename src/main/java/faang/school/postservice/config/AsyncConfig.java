package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${cache.authors.corePoolSize}")
    private int corePoolSize;

    @Value("${cache.authors.maxPoolSize}")
    private int maxPoolSize;

    @Value("${cache.authors.queueCapacity}")
    private int queueCapacity;

    @Value("${cache.authors.threadNamePrefix}")
    private String threadNamePrefix;


    @Bean(name = "postAuthorCacheExecutor")
    public Executor postAuthorCacheExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }

}

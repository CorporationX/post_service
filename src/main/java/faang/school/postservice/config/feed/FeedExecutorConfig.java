package faang.school.postservice.config.feed;

import faang.school.postservice.properties.feed.FeedExecutorProperties;
import faang.school.postservice.properties.feed.FeedHeaterExecutorProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class FeedExecutorConfig {

    @Bean(name = "feedTaskExecutor")
    public Executor feedTaskExecutor(FeedExecutorProperties properties) {
        return createExecutor(
                properties.getCorePoolSize(),
                properties.getMaxPoolSize(),
                properties.getQueueCapacity(),
                properties.getThreadNamePrefix()
        );
    }

    @Bean(name = "feedHeaterExecutor")
    public Executor feedHeaterExecutor(FeedHeaterExecutorProperties properties) {
        return createExecutor(
                properties.getCorePoolSize(),
                properties.getMaxPoolSize(),
                properties.getQueueCapacity(),
                properties.getThreadNamePrefix()
        );
    }

    private Executor createExecutor(int corePoolSize, int maxPoolSize, int queueCapacity, String threadPrefix) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadPrefix);
        executor.initialize();
        return executor;
    }
}

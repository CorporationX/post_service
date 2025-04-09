package faang.school.postservice.config.thread.feed;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class FeedThreadPoolConfig {

    @Value("${thread-pool.feed.core-pool-size}")
    private int feedThreadPoolSize;

    @Value("${thread-pool.feed.max-pool-size}")
    private int feedThreadMaxPoolSize;

    @Value("${thread-pool.feed.queue-capacity}")
    private int queueThreadPoolSize;

    @Bean(name = "feedExecutor")
    public Executor feedExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(feedThreadPoolSize);
        executor.setMaxPoolSize(feedThreadMaxPoolSize);
        executor.setQueueCapacity(queueThreadPoolSize);
        executor.setThreadNamePrefix("FeedExecutor-");
        executor.initialize();
        return executor;
    }
}

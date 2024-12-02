package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ThreadPoolConfig {

    @Value("${thread.pool.feed.size}")
    private int feedThreadPoolSize;

    @Value("${thread.pool.feed.max-size}")
    private int feedThreadMaxPoolSize;

    @Value("${thread.queue.size}")
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

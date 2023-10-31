package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class SpringAsyncConfig {

    @Value("${async.post-moderation.thread-pool.settings.core-pool-size}")
    private int postModCorePoolSize;
    @Value("${async.post-moderation.thread-pool.settings.max-pool-size}")
    private int postModMaxPoolSize;
    @Value("${async.post-moderation.thread-pool.settings.queue-capacity}")
    private int postModQueueCapacity;
    @Value("${async.feed-heating.thread-pool.settings.core-pool-size}")
    private int feedHeaterCorePoolSize;
    @Value("${async.feed-heating.thread-pool.settings.max-pool-size}")
    private int feedHeaterMaxPoolSize;
    @Value("${async.feed-heating.thread-pool.settings.queue-capacity}")
    private int feedHeaterQueueCapacity;

    @Bean("threadPoolForPostModeration")
    public Executor threadPoolForPostModeration() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(postModCorePoolSize);
        executor.setMaxPoolSize(postModMaxPoolSize);
        executor.setQueueCapacity(postModQueueCapacity);
        executor.initialize();
        return executor;
    }

    @Bean("feedHeaterThreadPool")
    public Executor feedHeaterThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(feedHeaterCorePoolSize);
        executor.setMaxPoolSize(feedHeaterMaxPoolSize);
        executor.setQueueCapacity(feedHeaterQueueCapacity);
        executor.initialize();
        return executor;
    }
}

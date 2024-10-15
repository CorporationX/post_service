package faang.school.postservice.config.async;

import faang.school.postservice.property.RedisAsyncProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class AsyncCacheConfig {

    private final RedisAsyncProperty redisAsyncProperty;

    @Bean
    public Executor postsCacheTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(redisAsyncProperty.getSettings().get("posts").getCorePoolSize());
        executor.setMaxPoolSize(redisAsyncProperty.getSettings().get("posts").getMaxPoolSize());
        executor.setQueueCapacity(redisAsyncProperty.getSettings().get("posts").getQueueCapacity());
        executor.setThreadNamePrefix("PostCacheAsyncThread-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Executor commentsCacheTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(redisAsyncProperty.getSettings().get("comments").getCorePoolSize());
        executor.setMaxPoolSize(redisAsyncProperty.getSettings().get("comments").getMaxPoolSize());
        executor.setQueueCapacity(redisAsyncProperty.getSettings().get("comments").getQueueCapacity());
        executor.setThreadNamePrefix("CommentsCacheAsyncThread-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Executor authorsCacheTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(redisAsyncProperty.getSettings().get("authors").getCorePoolSize());
        executor.setMaxPoolSize(redisAsyncProperty.getSettings().get("authors").getMaxPoolSize());
        executor.setQueueCapacity(redisAsyncProperty.getSettings().get("authors").getQueueCapacity());
        executor.setThreadNamePrefix("AuthorsCacheAsyncThread-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Executor feedCacheTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(redisAsyncProperty.getSettings().get("feed").getCorePoolSize());
        executor.setMaxPoolSize(redisAsyncProperty.getSettings().get("feed").getMaxPoolSize());
        executor.setQueueCapacity(redisAsyncProperty.getSettings().get("feed").getQueueCapacity());
        executor.setThreadNamePrefix("FeedCacheAsyncThread-");
        executor.initialize();
        return executor;
    }
}

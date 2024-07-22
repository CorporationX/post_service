package faang.school.postservice.config.async;

import faang.school.postservice.property.AsyncProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class AsyncConfig {

    private final AsyncProperty asyncProperty;

    @Bean
    public Executor hashtagTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncProperty.getSettings().get("hashtags").getCorePoolSize());
        executor.setMaxPoolSize(asyncProperty.getSettings().get("hashtags").getMaxPoolSize());
        executor.setQueueCapacity(asyncProperty.getSettings().get("hashtags").getQueueCapacity());
        executor.setThreadNamePrefix("HashtagAsyncThread-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Executor kafkaThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncProperty.getSettings().get("kafka").getCorePoolSize());
        executor.setMaxPoolSize(asyncProperty.getSettings().get("kafka").getMaxPoolSize());
        executor.setQueueCapacity(asyncProperty.getSettings().get("kafka").getQueueCapacity());
        executor.setThreadNamePrefix("KafkaAsyncThread-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Executor feedTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncProperty.getSettings().get("feed").getCorePoolSize());
        executor.setMaxPoolSize(asyncProperty.getSettings().get("feed").getMaxPoolSize());
        executor.setQueueCapacity(asyncProperty.getSettings().get("feed").getQueueCapacity());
        executor.setThreadNamePrefix("FeedAsyncThread-");
        executor.initialize();
        return executor;
    }

    @Bean
    public ExecutorService adRemoverExecutorService(@Value("${post.ad-remover.threads-count}") int threadsCount) {
        return Executors.newFixedThreadPool(threadsCount);
    }

    @Bean
    public ExecutorService commentModeratorExecutorService(@Value("${moderation.threads-count}") int threadsCount) {
        return Executors.newFixedThreadPool(threadsCount);
    }

    @Bean
    public ExecutorService executorService(@Value("${post.moderator.threads-count}") int threadsCount) {
        return Executors.newFixedThreadPool(threadsCount);
    }
}

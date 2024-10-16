package faang.school.postservice.config.async;

import faang.school.postservice.property.AsyncProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@RequiredArgsConstructor
public class AsyncConfig {

    @Value("${async.corePoolSize}")
    private int corePoolSize;

    @Value("${async.maxPoolSize}")
    private int maxPoolSize;

    @Value("${async.queueCapacity}")
    private int queueCapacity;

    private final AsyncProperty asyncProperty;

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("AsyncThread-");
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
}

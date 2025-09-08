package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ExecutorServiceConfig {
    @Value("${spring.kafka.topics.post-comment-publish.executor.core-pool-size}")
    private int generatorCorePoolSize;
    @Value("${spring.kafka.topics.post-comment-publish.executor.max-pool-size}")
    private int generatorMaxPoolSize;
    @Value("${spring.kafka.topics.post-comment-publish.executor.queue-capacity}")
    private int generatorQueueCapacity;



    @Bean("postCreateProducer")
    public Executor postCreateProducer() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(generatorCorePoolSize);
        executor.setMaxPoolSize(generatorMaxPoolSize);
        executor.setQueueCapacity(generatorQueueCapacity);
        executor.initialize();
        return executor;
    }
}
package faang.school.postservice.config.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class KafkaExecutorConfig {
    @Value("${kafka.topic.post.async.core-pool-size}")
    private Integer corePoolSize;
    @Value("${kafka.topic.post.async.max-pool-size}")
    private Integer maxPoolSize;
    @Value("${kafka.topic.post.async.queue-capacity}")
    private Integer queueCapacity;

    @Bean(name = "postToKafkaExecutor")
    public ThreadPoolTaskExecutor postToKafkaEventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("postEventExecutor-");
        executor.initialize();
        return executor;
    }
}

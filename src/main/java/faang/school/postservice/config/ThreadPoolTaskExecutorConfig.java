package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class ThreadPoolTaskExecutorConfig {

    @Value("${comment.moderation.maxThreadPoolSize}")
    private int maxThreadPoolSize;

    @Value("${comment.moderation.coreThreadPoolSize}")
    private int coreThreadPoolSize;

    @Value("${comment.moderation.queueCapacity}")
    private int queueCapacity;

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreThreadPoolSize);
        executor.setMaxPoolSize(maxThreadPoolSize);
        executor.setQueueCapacity(queueCapacity);
        return executor;
    }
}

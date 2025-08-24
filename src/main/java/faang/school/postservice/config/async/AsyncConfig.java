package faang.school.postservice.config.async;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class AsyncConfig {

    private final SendKafkaMessageExecutorProperties sendKafkaMessageExecutorProperties;

    @Bean(name = "correctDraftPosts")
    public Executor correctDraftPosts() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("RedisEventExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "sendKafkaMessageExecutor")
    public Executor sendKafkaMessageExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(sendKafkaMessageExecutorProperties.getCorePoolSize());
        executor.setMaxPoolSize(sendKafkaMessageExecutorProperties.getMaxPoolSize());
        executor.setQueueCapacity(sendKafkaMessageExecutorProperties.getQueueCapacity());
        executor.setThreadNamePrefix(sendKafkaMessageExecutorProperties.getPrefix());
        executor.initialize();
        return executor;
    }
}

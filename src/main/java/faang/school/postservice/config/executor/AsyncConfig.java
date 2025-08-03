package faang.school.postservice.config.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@RequiredArgsConstructor
@EnableAsync
@Configuration
public class AsyncConfig {

    private final PostKafkaPublishExecutorProperties postKafkaPublishExecutorProperties;


    @Bean(name = "postKafkaPublishExecutor")
    public Executor postKafkaPublishExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(postKafkaPublishExecutorProperties.getThreadNamePrefix());
        executor.setCorePoolSize(postKafkaPublishExecutorProperties.getCorePoolSize());
        executor.setMaxPoolSize(postKafkaPublishExecutorProperties.getMaxPoolSize());
        executor.setQueueCapacity(postKafkaPublishExecutorProperties.getQueueCapacity());
        return executor;
    }
}

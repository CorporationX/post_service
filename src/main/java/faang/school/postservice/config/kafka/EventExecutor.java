package faang.school.postservice.config.kafka;

import faang.school.postservice.config.kafka.properties.AsyncProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class EventExecutor {

    private final AsyncProperties asyncProperties;

    @Bean(name = "postEventExecutor")
    public Executor postEventExecutor() {
        AsyncProperties.ExecutorSettings settings = asyncProperties.getPostEventExecutor();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(settings.getCorePoolSize());
        executor.setMaxPoolSize(settings.getMaxPoolSize());
        executor.setQueueCapacity(settings.getQueueCapacity());
        executor.setThreadNamePrefix(settings.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }
}
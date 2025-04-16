package faang.school.postservice.config.taskexecutor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class TaskExecutorConfig {

    private final TaskExecutorProperties taskExecutorProperties;

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(taskExecutorProperties.getCorePoolSize());
        executor.setMaxPoolSize(taskExecutorProperties.getMaxPoolSize());
        executor.setQueueCapacity(taskExecutorProperties.getQueueCapacity());
        executor.initialize();
        return executor;
    }
}

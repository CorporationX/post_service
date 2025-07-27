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

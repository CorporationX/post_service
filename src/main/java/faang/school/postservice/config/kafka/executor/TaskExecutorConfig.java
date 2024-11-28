package faang.school.postservice.config.kafka.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@ConfigurationPropertiesScan
@Configuration("kafkaTaskExecutorConfig")
@RequiredArgsConstructor
public class TaskExecutorConfig {
    private final ProducerExecutorParams producerExecutorParams;

    @Bean
    public ThreadPoolTaskExecutor kafkaProducerConsumerExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(producerExecutorParams.getCorePoolSize());
        taskExecutor.setMaxPoolSize(producerExecutorParams.getMaxPoolSize());
        taskExecutor.setQueueCapacity(producerExecutorParams.getQueueCapacity());
        taskExecutor.setThreadNamePrefix(producerExecutorParams.getThreadNamePrefix());

        return taskExecutor;
    }
}

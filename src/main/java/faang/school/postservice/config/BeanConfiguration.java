package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class BeanConfiguration {

    @Value("${task.executor.pool-size}")
    private int poolSize;

    @Bean
    public ExecutorService taskExecutor() {
        return Executors.newFixedThreadPool(poolSize);
    }
}

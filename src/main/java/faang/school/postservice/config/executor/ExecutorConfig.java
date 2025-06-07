package faang.school.postservice.config.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {
    @Value("${executor-service.threads-count}")
    private int threadsCount;

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(threadsCount);
    }
}
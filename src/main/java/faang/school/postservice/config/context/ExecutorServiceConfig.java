package faang.school.postservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorServiceConfig {

    @Value("${executor.thread.pool.size}")
    private int threadCount;

    @Bean
    public ExecutorService getExecutorService() {
        return Executors.newFixedThreadPool(threadCount);
    }
}

package faang.school.postservice.config.executor;

import faang.school.postservice.config.properties.ExecutorServiceProperties;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class ExecutorConfig {
    private ExecutorService executorService;
    private final ExecutorServiceProperties executorProperties;

    @Bean
    public ExecutorService executorService() {
        this.executorService = Executors.newFixedThreadPool(executorProperties.getThreadsCount());
        return executorService;
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(executorProperties.getTerminationTimeout(), TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
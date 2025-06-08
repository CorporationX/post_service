package faang.school.postservice.config.executor;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorConfig {
    private ExecutorService executorService;

    @Value("${executor-service.threads-count}")
    private int threadsCount;

    @Value("${executor-service.termination-timeout}")
    private int terminationTimeout;

    @Bean
    public ExecutorService executorService() {
        this.executorService = Executors.newFixedThreadPool(threadsCount);
        return executorService;
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(terminationTimeout, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
package faang.school.postservice.config.executor;

import faang.school.postservice.config.properties.ExecutorServiceProperties;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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

    @Bean(name = "postExecutor")
    public ThreadPoolTaskExecutor postExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(executorProperties.getThreadsCount());
        executor.setMaxPoolSize(executorProperties.getThreadsCount());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(executorProperties.getTerminationTimeout());
        executor.initialize();
        return executor;
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
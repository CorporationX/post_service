package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ExecutorsPool {
    @Value("${executor.tread-count}")
    private int EXECUTOR_COUNT;

    @Bean(name = "taskExecutor", destroyMethod = "shutdown")
    public ExecutorService executor() {
        return Executors.newFixedThreadPool(EXECUTOR_COUNT);
    }
}
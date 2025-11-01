package faang.school.postservice.config;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ExecutorsPool {
    @Value("${executor.tread-count}")
    private int EXECUTOR_COUNT;
    @Value("${executor.await-time-seconds}")
    private int AWAIT_TIME_SECONDS;
    private ExecutorService executorService;

    @Bean(name = "taskExecutor", destroyMethod = "shutdown")
    public ExecutorService executor() {
        ExecutorService executor = Executors.newFixedThreadPool(EXECUTOR_COUNT);
        return executor;
    }

    @PreDestroy
    private void destroy() {
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(AWAIT_TIME_SECONDS, TimeUnit.SECONDS)) {
                    log.warn("Threads did not stop in {} seconds, stop forcibly", AWAIT_TIME_SECONDS);
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                log.warn("Error while waiting for threads to stop");
                executorService.shutdownNow();
            }
        }

    }
}
package faang.school.postservice.config;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class SchedulerExecutorService {
    @Value("${executor.thread-count}")
    private int executorCount;
    @Value("${executor.max-thread-pool}")
    private int maxThreadPool;
    @Value("${executor.await-time-seconds}")
    private int awaitTimeSecond;

    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(executorCount);
        executor.setMaxPoolSize(maxThreadPool);
        executor.setThreadNamePrefix("importTaskExecutor-");
        executor.setAwaitTerminationMillis(awaitTimeSecond);
        return executor;
    }
}
package faang.school.postservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class AsyncExecutor {

    private static final long MAX_WAIT_MILLIS = 300000;
    private static final int CORE_POOL_SIZE = 5;
    private static final int CORE_MAX_SIZE = 10;
    private static final int QUEUE_CAPACITY = 100;

    @Bean(name = "expiredAdTaskExecutor")
    public ThreadPoolTaskExecutor expiredAdTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setAwaitTerminationMillis(MAX_WAIT_MILLIS);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(CORE_MAX_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setThreadNamePrefix("expiredAdTaskExecutor-");
        executor.initialize();
        return executor;
    }
}

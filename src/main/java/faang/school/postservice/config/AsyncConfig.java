package faang.school.postservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AsyncConfig {

    @Value("${spring.task.execution.pool.post-event.core-size:5}")
    private int coreSize;

    @Value("${spring.task.execution.pool.post-event.max-size:10}")
    private int maxSize;

    @Value("${spring.task.execution.pool.post-event.queue-capacity:100}")
    private int queueCapacity;

    @Value("${spring.task.execution.pool.post-event.thread-name-prefix:PostEvent-}")
    private String threadNamePrefix;

    @Value("${spring.task.execution.pool.post-event.keep-alive-seconds:60}")
    private int keepAliveSeconds;

    @Value("${spring.task.execution.pool.post-event.await-termination-seconds:30}")
    private int awaitTerminationSeconds;

    @Bean(name = "postEventTaskExecutor")
    public Executor postEventTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        executor.initialize();
        return executor;
    }
}
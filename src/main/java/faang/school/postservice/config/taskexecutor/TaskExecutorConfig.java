package faang.school.postservice.config.taskexecutor;

import faang.school.postservice.config.properties.TaskExecutorProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TaskExecutorConfig {

    private final TaskExecutorProperties taskExecutorProperties;

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(taskExecutorProperties.getFileUpload().getCorePoolSize());
        executor.setMaxPoolSize(taskExecutorProperties.getFileUpload().getMaxPoolSize());
        executor.setQueueCapacity(taskExecutorProperties.getFileUpload().getQueueCapacity());
        executor.initialize();
        return executor;
    }

    @Bean(name = "redisReconnectionScheduler")
    public ThreadPoolTaskScheduler redisReconnectionScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("redis-reconnect-");
        scheduler.setDaemon(true);
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.setErrorHandler(t -> log.error("Error in Redis reconnection task", t));
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(10);
        return scheduler;
    }

    @Bean
    @Primary
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(8);
        scheduler.setThreadNamePrefix("scheduled-task-");
        scheduler.setDaemon(true);
        scheduler.setErrorHandler(t -> log.error("Error in scheduled task", t));
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(10);
        return scheduler;
    }
}

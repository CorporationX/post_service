package faang.school.postservice.config.taskexecutor;

import faang.school.postservice.config.properties.TaskExecutorProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        var props = taskExecutorProperties.getFileUpload();
        executor.setCorePoolSize(props.getCorePoolSize());
        executor.setMaxPoolSize(props.getMaxPoolSize());
        executor.setQueueCapacity(props.getQueueCapacity());
        executor.initialize();
        return executor;
    }

    @Bean(name = "outboxEventPublisherExecutor")
    public ThreadPoolTaskExecutor outboxEventPublisherExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        var props = taskExecutorProperties.getOutboxEventPublisherTask();
        executor.setCorePoolSize(props.getCorePoolSize());
        executor.setMaxPoolSize(props.getMaxPoolSize());
        executor.setQueueCapacity(props.getQueueCapacity());
        executor.setThreadNamePrefix(props.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }

    @Bean(name = "redisReconnectionScheduler")
    public ThreadPoolTaskScheduler redisReconnectionScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        var props = taskExecutorProperties.getRedisReconnect();
        scheduler.setPoolSize(props.getPoolSize());
        scheduler.setThreadNamePrefix(props.getThreadNamePrefix());
        scheduler.setDaemon(props.getDaemon());
        scheduler.setRemoveOnCancelPolicy(props.getRemoveOnCancelPolicy());
        scheduler.setErrorHandler(t -> log.error("Error in Redis reconnection task", t));
        scheduler.setWaitForTasksToCompleteOnShutdown(props.getWaitForTasksToCompleteOnShutdown());
        scheduler.setAwaitTerminationSeconds(props.getAwaitTerminationSeconds());
        return scheduler;
    }

    @Bean(name = "scheduledTaskScheduler")
    public ThreadPoolTaskScheduler scheduledTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        var props = taskExecutorProperties.getScheduledTask();
        scheduler.setPoolSize(props.getPoolSize());
        scheduler.setThreadNamePrefix(props.getThreadNamePrefix());
        scheduler.setDaemon(props.getDaemon());
        scheduler.setErrorHandler(t -> log.error("Error in scheduled task", t));
        scheduler.setWaitForTasksToCompleteOnShutdown(props.getWaitForTasksToCompleteOnShutdown());
        scheduler.setAwaitTerminationSeconds(props.getAwaitTerminationSeconds());
        return scheduler;
    }
}

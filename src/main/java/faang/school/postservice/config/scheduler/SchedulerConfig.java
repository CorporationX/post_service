package faang.school.postservice.config.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
@Slf4j
public class SchedulerConfig {

    @Value("${spring.scheduling.pool.size}")
    private int poolSize;

    @Value("${spring.scheduling.thread-name-prefix}")
    private String threadNamePrefix;

    @Value("${spring.scheduling.shutdown.await-termination-period}")
    private int awaitTerminationSeconds;

    @Value("${spring.scheduling.shutdown.await-termination}")
    private boolean isAwaitTermination;

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(poolSize);
        scheduler.setThreadNamePrefix(threadNamePrefix);
        scheduler.setAwaitTerminationSeconds(awaitTerminationSeconds);
        scheduler.setWaitForTasksToCompleteOnShutdown(isAwaitTermination);
        scheduler.setErrorHandler(t -> log.error("Error at scheduler: ", t));
        scheduler.setRemoveOnCancelPolicy(true);
        return scheduler;
    }
}

package faang.school.postservice.config.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@EnableScheduling
@RequiredArgsConstructor
public class ScheduledPostsExecutorConfig {

    @Value("${scheduled-post-publisher.executor.thread_pool_core_size}")
    private int corePoolSize;

    @Value("${scheduled-post-publisher.executor.thread_pool_max_size}")
    private int maximumPoolSize;

    @Bean(name = "scheduledPostsExecutor")
    public ThreadPoolTaskExecutor scheduledPostsExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maximumPoolSize);
        executor.initialize();
        return executor;
    }
}
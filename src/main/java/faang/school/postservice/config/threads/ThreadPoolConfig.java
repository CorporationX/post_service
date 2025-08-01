package faang.school.postservice.config.threads;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {

    private final ThreadPoolProperties threadPoolProperties;
    private final NewsfeedPoolProperties newsfeedPoolProperties;

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolProperties.coreSize());
        executor.setMaxPoolSize(threadPoolProperties.maxSize());
        executor.setThreadNamePrefix(threadPoolProperties.prefix());
        executor.initialize();
        return executor;
    }

    @Bean
    public ThreadPoolTaskExecutor newsFeedExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(newsfeedPoolProperties.coreSize());
        executor.setMaxPoolSize(newsfeedPoolProperties.maxSize());
        executor.setThreadNamePrefix(newsfeedPoolProperties.prefix());
        executor.initialize();
        return executor;
    }
}

package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AuthorBannerPoolConfig {

    @Value("${thread-pool.author-ban.size}")
    private int poolSize;

    @Value("${thread-pool.author-ban.shutdown-timeout-seconds}")
    private int shutdownTimeoutSeconds;

    @Bean(name = "authorBannerPool")
    public ThreadPoolTaskExecutor authorBannerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setThreadNamePrefix("AuthorBannerPool-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(shutdownTimeoutSeconds);
        executor.initialize();
        return executor;
    }
}

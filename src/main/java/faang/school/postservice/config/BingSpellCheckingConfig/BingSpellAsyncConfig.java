package faang.school.postservice.config.BingSpellCheckingConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class BingSpellAsyncConfig {
    @Bean(name = "bingSpellAsyncExecutor")
    public ThreadPoolTaskExecutor bingSpellAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(1000000);
        executor.initialize();
        return executor;
    }
}
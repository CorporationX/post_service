package faang.school.postservice.config.postCorrecter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class BingSpellAsyncConfig {
    @Bean
    public ThreadPoolTaskExecutor bingSpellAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100000);
        executor.initialize();
        return executor;
    }
}

package faang.school.postservice.config.postCorrecter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class BingSpellAsyncConfig {
    private BingSpellThreadPoolConfig poolConfig;

    @Bean
    public ThreadPoolTaskExecutor bingSpellAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolConfig.getCorePoolSize());
        executor.setMaxPoolSize(poolConfig.getMaxPoolSize());
        executor.setQueueCapacity(poolConfig.getQueueCapacity());
        executor.initialize();
        return executor;
    }
}

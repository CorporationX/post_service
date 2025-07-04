package faang.school.postservice.config.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ConfigExecutorForPostService {

    @Value("${app.executor.thread_pool_max_size}")
    private int maxThreadPoolSize;

    @Value("${app.executor.thread_pool_core_size}")
    private int corePoolSize;

    @Bean
    public ThreadPoolTaskExecutor executorForPostService() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxThreadPoolSize);
        executor.initialize();
        return executor;
    }
}
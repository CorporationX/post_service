package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class PostThreadPoolConfig {
    @Value("${post.pool-size}")
    private Integer postPoolSize;

    @Bean
    public Executor postExecutorPool() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setMaxPoolSize(postPoolSize);
        threadPoolTaskExecutor.setCorePoolSize(postPoolSize);
        threadPoolTaskExecutor.setThreadNamePrefix("CustomThreadPool-");
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}

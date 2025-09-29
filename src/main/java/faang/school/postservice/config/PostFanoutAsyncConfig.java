package faang.school.postservice.config;

import faang.school.postservice.config.properties.cache.post.PostFanoutExecutorProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class PostFanoutAsyncConfig {

    private final PostFanoutExecutorProperties postFanoutExecutorProperties;

    @Bean(name = "postFanoutExecutor")
    public ThreadPoolTaskExecutor postFanoutExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(postFanoutExecutorProperties.coreSize());
        ex.setMaxPoolSize(postFanoutExecutorProperties.maxSize());
        ex.setQueueCapacity(postFanoutExecutorProperties.queueCapacity());
        ex.initialize();
        return ex;
    }
}

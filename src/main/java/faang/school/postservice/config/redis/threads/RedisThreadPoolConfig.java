package faang.school.postservice.config.redis.threads;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class RedisThreadPoolConfig {

    @Bean("redisTaskExecutor")
    public ThreadPoolTaskExecutor redisTaskExecutor(RedisThreadPoolProperties redisThreadPoolProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(redisThreadPoolProperties.coreSize());
        executor.setMaxPoolSize(redisThreadPoolProperties.maxSize());
        executor.setThreadNamePrefix(redisThreadPoolProperties.prefix());
        executor.initialize();
        return executor;
    }
}
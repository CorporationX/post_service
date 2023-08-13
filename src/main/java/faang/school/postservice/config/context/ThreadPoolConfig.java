package faang.school.postservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    @Value("${post.config.thread-pool.core-pool-size}")
    private int corePoolSize;

    @Value("${post.config.thread-pool.maximum-pool-size}")
    private int maximumPoolSize;

    @Value("${post.config.thread-pool.keep-alive-time}")
    private int keepAliveTime;

    @Value("${post.config.thread-pool.time-unit}")
    private String timeUnit;

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.valueOf(timeUnit),
                new LinkedBlockingQueue<>()
        );
    }
}

package faang.school.postservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ScheduledPostPublisherThreadPoolConfig {

    @Value("${post.publisher.scheduler.thread-pool.core-pool-size}")
    private int corePoolSize;

    @Value("${post.publisher.scheduler.thread-pool.maximum-pool-size}")
    private int maximumPoolSize;

    @Value("${post.publisher.scheduler.thread-pool.keep-alive-time}")
    private int keepAliveTime;

    @Value("${post.publisher.scheduler.thread-pool.time-unit}")
    private String timeUnit;

    @Bean
    public ThreadPoolExecutor scheduledPostPublisherThreadPoolExecutor() {
        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.valueOf(timeUnit),
                new LinkedBlockingQueue<>()
        );
    }
}
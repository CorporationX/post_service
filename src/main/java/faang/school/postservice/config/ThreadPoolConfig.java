package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {
    @Value("${pool.notice-pool.core-pool-size}")
    private int noticePoolSize;

    @Value("${pool.notice-pool.maximum-pool-size}")
    private int noticeMaxPoolSize;

    @Value("${pool.notice-pool.keep-alive-time}")
    private int noticeLiveTime;

    @Value("${pool.notice-pool.capacity}")
    private int noticeCapacity;

    @Bean
    public ExecutorService noticePool(){

        return new ThreadPoolExecutor(noticePoolSize, noticeMaxPoolSize, noticeLiveTime, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(noticeCapacity));
    }
}

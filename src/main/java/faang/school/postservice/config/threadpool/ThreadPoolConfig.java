package faang.school.postservice.config.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {

    @Value("${post.correcter.thread-pool-size}")
    private int threadPoolSize;

    @Bean
    public ExecutorService fixedThreadPool() {
        return Executors.newFixedThreadPool(threadPoolSize);
    }
}

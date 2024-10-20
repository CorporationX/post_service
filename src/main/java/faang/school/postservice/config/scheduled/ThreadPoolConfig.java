package faang.school.postservice.config.scheduled;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {
    @Value("${post.config.thread-pool.max-size}")
    private int maxSize;

    @Bean(name = "treadPool")
    public ExecutorService threadPool() {
        return Executors.newFixedThreadPool(maxSize);
    }
}

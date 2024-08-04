package faang.school.postservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Evgenii Malkov
 */
@Configuration
public class ThreadPoolConfig {
    @Value("${post.publisher.thread-pool-size:10}")
    private int threadPoolSize;

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(threadPoolSize);
    }
}

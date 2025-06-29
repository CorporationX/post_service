package faang.school.postservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {

    @Value("${thread-pool.publish-posts-max-threads}")
    private int publishThreadSize;

    @Value("${thread-pool.heater-feed-size}")
    private int feedHeatPoolSize;

    @Bean(destroyMethod = "shutdown")
    public ExecutorService threadPool() {
        return Executors.newFixedThreadPool(publishThreadSize);
    }

    @Bean(name = "feedHeaterExecutor")
    public ExecutorService feedHeaterExecutor() {
        return Executors.newFixedThreadPool(feedHeatPoolSize);
    }
}

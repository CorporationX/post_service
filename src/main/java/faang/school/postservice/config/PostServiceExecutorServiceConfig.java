package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class PostServiceExecutorServiceConfig {

    @Value("${post-service.post-distribution.thread-count}")
    private int threadCount;

    @Bean
    public ExecutorService postServiceExecutorService() {
        return Executors.newFixedThreadPool(threadCount);
    }
}

package faang.school.postservice.config.post;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class PublishingConfig {
    @Value("${scheduled-publication.thread-pool-size}")
    private int fixedThreadPoolSize;

    @Bean(name = "publishPostThreadPool")
    public ExecutorService publishPostThreadPool() {
        return Executors.newFixedThreadPool(fixedThreadPoolSize);
    }
}

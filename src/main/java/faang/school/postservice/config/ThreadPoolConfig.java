package faang.school.postservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {
    @Bean
    public ExecutorService publishedPostThreadPool() {
        ExecutorService service = Executors.newFixedThreadPool(10);
        return Executors.newFixedThreadPool(10);
    }
}
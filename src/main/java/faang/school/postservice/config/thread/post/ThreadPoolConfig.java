package faang.school.postservice.config.thread.post;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {

    @Bean(name = "postModerationThreadPool")
    public ExecutorService customThreadPool() {
        return Executors.newFixedThreadPool(10);
    }
}

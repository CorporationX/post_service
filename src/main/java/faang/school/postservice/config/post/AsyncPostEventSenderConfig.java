package faang.school.postservice.config.post;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class AsyncPostEventSenderConfig {
    @Value("${post-event.thread-pool-size}")
    private int fixedThreadPoolSize;

    @Bean
    public ExecutorService postEventSenderThreadPool() {
        return Executors.newFixedThreadPool(fixedThreadPoolSize);
    }
}

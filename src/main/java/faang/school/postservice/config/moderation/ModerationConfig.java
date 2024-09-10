package faang.school.postservice.config.moderation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ModerationConfig {

    @Value("${comment.thread-pool-size}")
    private int threadPoolSize;

    @Bean("moderation-thread-pool")
    public ExecutorService moderationExecutor() {
        return Executors.newFixedThreadPool(threadPoolSize);
    }
}

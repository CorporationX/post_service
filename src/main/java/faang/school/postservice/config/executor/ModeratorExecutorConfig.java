package faang.school.postservice.config.executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ModeratorExecutorConfig {
    @Bean
    public ExecutorService moderatorExecutor() {
        return Executors.newCachedThreadPool();
    }
}
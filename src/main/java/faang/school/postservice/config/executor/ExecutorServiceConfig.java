package faang.school.postservice.config.executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorServiceConfig {

    @Bean
    @Primary
    public ExecutorService cachedExecutor() {
        return Executors.newCachedThreadPool();
    }
}

package faang.school.postservice.config.pool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ExecutorServiceConfig {

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(10);
    }
}

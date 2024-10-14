package faang.school.postservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class SchedulingConfig {
    @Bean
    public ExecutorService schedulingThreadPoolExecutor() {
        return Executors.newFixedThreadPool(10);
    }
}

package faang.school.postservice.config.poolThread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ScheduledPoolConfig {
    @Bean
    public ExecutorService poolForCron(){
        return Executors.newSingleThreadScheduledExecutor();
    }
}
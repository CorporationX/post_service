package faang.school.postservice.config.pool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {

    @Value("${task-scheduler.fixedThread.count}")
    private int countThread;

    @Bean
    public ExecutorService executor() {
        return Executors.newFixedThreadPool(countThread);
    }
}

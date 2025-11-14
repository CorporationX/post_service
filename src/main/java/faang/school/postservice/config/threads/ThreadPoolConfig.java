package faang.school.postservice.config.threads;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class ThreadPoolConfig {

    @Value("${app.scheduled-posts.thread-pool-size:10}")
    private int threadPoolSize;

    @Bean
    public ExecutorService scheduledPostExecutor() {
        return Executors.newFixedThreadPool(threadPoolSize);
    }
}

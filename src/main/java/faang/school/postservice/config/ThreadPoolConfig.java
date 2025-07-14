package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Slf4j
public class ThreadPoolConfig {

    @Value("${scheduler.thread-pool-size}")
    private int threadPoolSize;

    @Bean
    public ExecutorService scheduledPostExecutorService() {
        log.info("Creating thread pool with {} threads for scheduled post publishing",
                threadPoolSize);
        return Executors.newFixedThreadPool(threadPoolSize);
    }
}
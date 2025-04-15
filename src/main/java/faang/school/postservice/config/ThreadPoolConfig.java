package faang.school.postservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {

    @Value("${thread-pool.publish-posts-max-threads}")
    private int publishThreadSize;

    @Bean(destroyMethod = "shutdown")
    public ExecutorService threadPool() {
        return Executors.newFixedThreadPool(publishThreadSize);
    }
}

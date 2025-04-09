package faang.school.postservice.config.thread;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {

    @Value("${thread-pool.max-threads}")
    private Integer threadsNumber;

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(threadsNumber);
    }
}

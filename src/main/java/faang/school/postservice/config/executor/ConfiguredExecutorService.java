package faang.school.postservice.config.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@RequiredArgsConstructor
public class ConfiguredExecutorService {

    @Value("${moderation.executor.thread_pool_size}")
    private int maxThreadPoolSize;

    @Bean
    public ExecutorService taskExecutor() {
        return Executors.newFixedThreadPool(maxThreadPoolSize);
    }


}

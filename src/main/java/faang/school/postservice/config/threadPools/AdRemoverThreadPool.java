package faang.school.postservice.config.threadPools;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class AdRemoverThreadPool {
    private final Integer threadPoolSize;

    public AdRemoverThreadPool(@Value("${ad-remover.thread-pool.size}") Integer threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    @Bean
    public ExecutorService newAdRemoverThreadPool() {
        return Executors.newFixedThreadPool(threadPoolSize);
    }
}

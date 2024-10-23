package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {

    @Value("${postServiceThreadPool.poolAmount}")
    private int nThreads;

    @Value("${postServiceThreadPool.poolHeatCache}")
    private int mThreads;

    @Value("${postServiceThreadPool.poolFeed}")
    private int kThreads;

    @Bean
    public ExecutorService postServicePool() {
        return Executors.newFixedThreadPool(nThreads);
    }

    @Bean
    public ExecutorService heaterPool() {
        return Executors.newFixedThreadPool(mThreads);
    }

    @Bean
    public ExecutorService feedPool() {
        return Executors.newFixedThreadPool(kThreads);
    }

}

package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {

    @Value("${app.feed.thread-pool-size}")
    private int feedThreadPoolSize;

    @Bean(name = "feedNextPostBatchExecutor")
    public ExecutorService feedNextPostBatchExecutor() {
        return Executors.newFixedThreadPool(feedThreadPoolSize);
    }

    @Bean(name = "feedNextCommentBatchExecutor")
    public ExecutorService feedNextCommentBatchExecutor() {
        return Executors.newFixedThreadPool(feedThreadPoolSize);
    }
}

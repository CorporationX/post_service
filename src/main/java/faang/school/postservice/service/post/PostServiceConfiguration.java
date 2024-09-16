package faang.school.postservice.service.post;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class PostServiceConfiguration {
    @Value("${post.thread-pool.size}")
    private int threadPoolSize;

    @Value("${post.thread-pool.task-timeout}")
    private long taskTimeout;

    @Bean("post-service-thread-pool")
    public ExecutorService threadPool() {
        return Executors.newFixedThreadPool(threadPoolSize);
    }

    @Bean
    public int taskTimeout(){
        return (int) taskTimeout;
    }
}

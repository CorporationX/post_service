package faang.school.postservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class PublisherExecutorService {

    @Value("${spring.kafka.topics.feed.executor:2}")
    private int kafkaExecutorPoolSize;

    @Bean
    public ExecutorService kafkaPublisherExecutor() {
        return Executors.newFixedThreadPool(kafkaExecutorPoolSize);
    }
}

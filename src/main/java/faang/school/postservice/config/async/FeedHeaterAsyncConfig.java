package faang.school.postservice.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class FeedHeaterAsyncConfig {

    @Bean
    public ExecutorService feedHeaterPool() {
        return Executors.newFixedThreadPool(5);
    }
}

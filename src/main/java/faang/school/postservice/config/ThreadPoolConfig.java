package faang.school.postservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Bean(name = "postPublishingThreadPool")
    public ExecutorService poolForScheduled() {
        return Executors.newFixedThreadPool(10);
    }

}

package faang.school.postservice.config.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class AppConfig {

    @Bean
    @Primary
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(10);
    }
}

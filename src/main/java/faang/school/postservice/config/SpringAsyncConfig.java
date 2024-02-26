package faang.school.postservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class SpringAsyncConfig {

    @Bean
    public ExecutorService executorService(){
        return Executors.newCachedThreadPool();
    }
}

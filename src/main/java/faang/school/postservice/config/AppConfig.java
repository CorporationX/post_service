package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {

    @Value("${threads.delete-ads-pool-size:3}")
    private int deleteAdsPools;

    @Bean
    @Qualifier("deleteAdsPool")
    public ExecutorService deleteAdsPool() {
        return Executors.newFixedThreadPool(deleteAdsPools);
    }
}
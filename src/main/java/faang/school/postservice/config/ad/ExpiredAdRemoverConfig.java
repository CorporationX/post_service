package faang.school.postservice.config.ad;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExpiredAdRemoverConfig {

    @Value("${scheduler.expired-ad.thread-pool}")
    private int pool;

    @Bean
    public ExecutorService clearAdsThreadPool() {
        return Executors.newFixedThreadPool(pool);
    }

}

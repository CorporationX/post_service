package faang.school.postservice.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolForNewsFeedAlgo {

    @Value("${postServiceThreadPool.poolFeedAlgo}")
    private int pullNumbers;

    @Bean
    public ExecutorService newsFeedAloPool() {
        return Executors.newFixedThreadPool(pullNumbers);
    }
}

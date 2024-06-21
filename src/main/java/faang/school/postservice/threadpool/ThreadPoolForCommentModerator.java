package faang.school.postservice.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolForCommentModerator {

    @Value("${postServiceThreadPool.poolComment}")
    private int pullNumbers;

    @Value("${postServiceThreadPool.poolAmount}")
    private int startNumbers;


    @Bean
    public ExecutorService taskExecutor() {
        return Executors.newFixedThreadPool(pullNumbers);
    }

    @Bean
    public ExecutorService startExecutor() {
        return Executors.newFixedThreadPool(startNumbers);
    }
}

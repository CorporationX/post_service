package faang.school.postservice.threadPool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolForCommentModerator {

    @Value("${pull.pullForCommentController}")
    private int pullNumbers;

    @Bean
    public ExecutorService taskExecutor() {
        return Executors.newFixedThreadPool(pullNumbers);
    }
}

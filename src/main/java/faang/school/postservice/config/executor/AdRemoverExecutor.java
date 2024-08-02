package faang.school.postservice.config.executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class AdRemoverExecutor {

    private final int MINIMUM_CORE_SIZE = 1;
    private final int MAXIMUM_CORE_SIZE = 50;
    private final int CORE_ALIVE_TIME = 60;
    private final int QUEUE_CAPACITY = 20;


    @Bean(name = "adRemover")
    public ExecutorService adRemover() {
        return new ThreadPoolExecutor(MINIMUM_CORE_SIZE,
                MAXIMUM_CORE_SIZE,
                CORE_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(QUEUE_CAPACITY));
    }
}

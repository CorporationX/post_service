package faang.school.postservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;


@Configuration
@EnableAsync
public class CustomThreadExecutor {

    @Value("${spring.task.execution.pool.core-size}")
    private int coreSize;
    @Value("${spring.task.execution.pool.max-size}")
    private int maxSize;
    @Value("${spring.task.execution.pool.queue-capacity}")
    private int queueCapacity;
    @Value("${spring.task.execution.pool.keep-alive}")
    private int keepAlive;
    @Value("${spring.task.execution.pool.thread-name-prefix}")
    private String threadName;

    @Bean(name = "asyncExecutor")
    public ThreadPoolExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAlive);
        executor.setThreadNamePrefix(threadName);
        executor.initialize();
        return executor.getThreadPoolExecutor();
    }
}

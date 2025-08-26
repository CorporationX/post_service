package faang.school.postservice.config;

import faang.school.postservice.config.properties.FeedHeaterProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class FeedHeaterThreadPoolConfig {

    @Bean(name = "feedHeaterExecutor")
    public TaskExecutor feedHeaterExecutor(FeedHeaterProperties props) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(props.getThreads());
        executor.setMaxPoolSize(props.getThreads());
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("feed-heater-");
        executor.initialize();
        return executor;
    }
}

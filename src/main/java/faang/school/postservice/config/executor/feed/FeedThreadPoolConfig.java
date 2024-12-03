package faang.school.postservice.config.executor.feed;

import faang.school.postservice.config.properties.feed.FeedProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class FeedThreadPoolConfig {

    private FeedProperties feedProperties;

    @Bean(name = "feedHeaterExecutor")
    public TaskExecutor taskExecutor() {
        int initialPoolSize = feedProperties.getThreadPool().getInitialPoolSize();
        int maxPoolSize = feedProperties.getThreadPool().getMaxPoolSize();
        int queueCapacity = feedProperties.getQueue().getCapacity();
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(initialPoolSize);
        taskExecutor.setMaxPoolSize(maxPoolSize);
        taskExecutor.setQueueCapacity(queueCapacity);
        taskExecutor.setThreadNamePrefix("FeedHeaterThread-");
        taskExecutor.initialize();
        return taskExecutor;
    }
}

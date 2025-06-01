package faang.school.postservice.config;

import faang.school.postservice.config.properties.ViewCountUpdaterPoolProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class ViewCountUpdaterPoolConfig {

    private final ViewCountUpdaterPoolProperties properties;

    @Bean(name = "viewCountUpdaterExecutor")
    public ThreadPoolTaskExecutor postPublisherExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.corePoolSize());
        executor.setMaxPoolSize(properties.maxPoolSize());
        executor.setThreadNamePrefix(properties.threadNamePrefix());
        executor.initialize();
        return executor;
    }
}

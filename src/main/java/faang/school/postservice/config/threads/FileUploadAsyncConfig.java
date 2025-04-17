package faang.school.postservice.config.threads;

import faang.school.postservice.config.ModerationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class FileUploadAsyncConfig {
    private final ModerationProperties moderationProperties;

    @Bean(name = "asyncModerationExecutor")
    public ThreadPoolTaskExecutor asyncModerationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(moderationProperties.getCorePoolSize());
        executor.setMaxPoolSize(moderationProperties.getMaxPoolSize());
        executor.setQueueCapacity(moderationProperties.getQueueCapacity());
        executor.setThreadNamePrefix("FileUploadAsync-");
        executor.initialize();
        return executor;
    }
}

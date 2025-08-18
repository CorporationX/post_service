package faang.school.postservice.config.spellcheck;

import faang.school.postservice.config.properties.SpellCheckAsyncProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class SpellCheckAsyncConfig {

    private final SpellCheckAsyncProperties properties;

    private static final String THREAD_NAME_PREFIX = "PostCorrection-";

    @Bean(name = "postCorrectionExecutor")
    public ThreadPoolTaskExecutor postCorrectionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.coreSize());
        executor.setMaxPoolSize(properties.maxSize());
        executor.setQueueCapacity(properties.queueCapacity());
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        executor.initialize();
        return executor;
    }
}

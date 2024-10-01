package faang.school.postservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class SpellCheckAsyncConfig {

    @Value("${spell-checker.queue-capacity}")
    private int correcterQueueCapacity;

    @Bean(name = "spellCheckAsyncExecutor")
    public Executor spellCheckAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(correcterQueueCapacity);
        executor.setThreadNamePrefix("SpellCheckAsyncExecutor-");
        executor.initialize();
        return executor;
    }
}

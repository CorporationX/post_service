package faang.school.postservice.bjs_77879;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;

@Configuration
public class ThreadPoolConfig {

    @Bean("adCleanupExecutor")
    public ExecutorService taskExecutor(AdCleanupSettings settings) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(settings.getThreadPool().getCorePoolSize());
        executor.setMaxPoolSize(settings.getThreadPool().getMaxPoolSize());
        executor.setQueueCapacity(settings.getThreadPool().getQueueCapacity());
        executor.setThreadNamePrefix("AdCleanup-");
        executor.initialize();
        return (ExecutorService) executor;
    }
}
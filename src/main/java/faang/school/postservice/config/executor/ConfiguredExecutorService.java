package faang.school.postservice.config.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class ConfiguredExecutorService {

    @Value("${moderation.executor.thread_pool_max_size}")
    private int maxThreadPoolSize;

    @Value("${moderation.executor.thread_pool_core_size}")
    private int corePoolSize;


    @Bean(name = "taskExecutor")
    @Primary
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxThreadPoolSize);
        executor.initialize();
        return executor;
    }


}

package faang.school.postservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class SpringAsyncConfig {

    @Value("${commenter-banner.thread-pool-size}")
    private int commenterBannerThreadPoolSize;

    @Bean(name = "commenterBannerExecutor")
    public Executor commenterBannerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("CommenterBanner-");
        executor.setCorePoolSize(commenterBannerThreadPoolSize);
        executor.initialize();
        return executor;
    }
}

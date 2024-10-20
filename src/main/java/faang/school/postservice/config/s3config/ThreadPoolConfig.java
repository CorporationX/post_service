package faang.school.postservice.config.s3config;

import faang.school.postservice.config.properties.ThreadPoolProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {

  private final ThreadPoolProperties threadPoolProperties;

    @Bean
    public ExecutorService executorService() {
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(threadPoolProperties.getCapacity());
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                threadPoolProperties.getCorePoolSize(),threadPoolProperties.getMaximumPoolSize(),
                threadPoolProperties.getKeepAliveTime(), TimeUnit.SECONDS,
                queue,
                new ThreadPoolExecutor.AbortPolicy()
        );
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }
}

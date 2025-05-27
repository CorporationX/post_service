package faang.school.postservice.config.kafka;

import faang.school.postservice.config.kafka.properties.RetryProperties;
import faang.school.postservice.config.kafka.properties.ThreadPoolProperties;
import faang.school.postservice.exception.UserServiceException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableScheduling
public class RetryConfig {

    private final RetryProperties retryProperties;
    private final ThreadPoolProperties threadPoolProperties;

    public RetryConfig(RetryProperties retryProperties, ThreadPoolProperties threadPoolProperties) {
        this.retryProperties = retryProperties;
        this.threadPoolProperties = threadPoolProperties;
    }

    @Bean
    public RetryTemplate userServiceRetryTemplate() {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(retryProperties.getUserService().getMaxAttempts());

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(retryProperties.getUserService().getDelay());
        backOffPolicy.setMultiplier(retryProperties.getUserService().getMultiplier());

        return RetryTemplate.builder()
                .customPolicy(retryPolicy)
                .customBackoff(backOffPolicy)
                .retryOn(UserServiceException.class)
                .build();
    }

    @Bean(name = "postEventExecutor")
    public Executor postEventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        ThreadPoolProperties.ExecutorProperties props = threadPoolProperties.getPostEventExecutor();
        executor.setCorePoolSize(props.getCorePoolSize());
        executor.setMaxPoolSize(props.getMaxPoolSize());
        executor.setQueueCapacity(props.getQueueCapacity());
        executor.setThreadNamePrefix(props.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }
}

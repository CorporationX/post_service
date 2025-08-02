package faang.school.postservice.config.redis.threads;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("thread.redis.pool")
public record RedisThreadPoolProperties(
        int coreSize,
        int maxSize,
        String prefix
) {
}
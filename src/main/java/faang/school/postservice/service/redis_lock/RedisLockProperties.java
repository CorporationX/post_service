package faang.school.postservice.service.redis_lock;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.redis.lock")
public class RedisLockProperties {

    private long defaultTimeoutMs;
    private long waitTimeoutMs;
    private long retryIntervalMs;
    private int maxRetries;
}

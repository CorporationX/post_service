package faang.school.postservice.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.data.redis.lock-registry")
public class RedisLockRegistryProperty {

    private String postLockKey;
    private Long releaseTimeDurationMillis;
}

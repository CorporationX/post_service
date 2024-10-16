package faang.school.postservice.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "spring.data.redis.lock-registry")
public class RedisLockRegistryProperty {

    private Map<String, Register> lockSettings;

    @Data
    public static class Register {

        private String postLockKey;
        private Long releaseTimeDurationMillis;
    }
}

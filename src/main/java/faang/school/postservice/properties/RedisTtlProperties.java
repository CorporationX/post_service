package faang.school.postservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.data.redis-cache")
@Data
public class RedisTtlProperties {
    private long defaultDays;
    private long lockTimeoutSeconds;
}
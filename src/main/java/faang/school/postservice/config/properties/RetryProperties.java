package faang.school.postservice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "spring.retry")
@Component
@Data
public class RetryProperties {
    private int maxAttempts;
    private long delay;
}
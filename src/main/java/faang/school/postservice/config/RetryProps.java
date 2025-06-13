package faang.school.postservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "retry")
public class RetryProps {
    private final int maxAttempts;
    private final long backoffDelay;
    private final double backoffMultiplier;
}

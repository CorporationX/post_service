package faang.school.postservice.config.kafka.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.retry")
public class RetryProperties {
    private RetrySettings userService;
    private RetrySettings kafka;

    @Data
    public static class RetrySettings {
        private int maxAttempts;
        private long delay;
        private double multiplier;
    }
}
package faang.school.postservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "retry.text-auto-correction-api")
public class RetryTextCorrectionProps {
    private int maxAttempts;
    private Backoff backoff;

    @Data
    public static class Backoff {
        private long delay;
        private double multiplier;
    }
}

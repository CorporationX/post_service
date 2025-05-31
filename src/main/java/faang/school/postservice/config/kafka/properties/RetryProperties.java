package faang.school.postservice.config.kafka.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "app.retry")
public class RetryProperties {
    @NotNull(message = "userService retry settings must be specified")
    private RetrySettings userService;

    @NotNull(message = "kafka retry settings must be specified")
    private RetrySettings kafka;

    @Data
    public static class RetrySettings {
        @NotNull(message = "maxAttempts must be specified")
        @Positive(message = "maxAttempts must be positive")
        @Min(value = 1, message = "maxAttempts must be at least 1")
        private Integer maxAttempts;

        @NotNull(message = "delay must be specified")
        @Positive(message = "delay must be positive")
        @Min(value = 1, message = "delay must be at least 1")
        private Long delay;

        @NotNull(message = "multiplier must be specified")
        @Positive(message = "multiplier must be positive")
        @Min(value = 1, message = "multiplier must be at least 1")
        private Double multiplier;
    }
}
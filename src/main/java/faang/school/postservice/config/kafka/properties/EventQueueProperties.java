package faang.school.postservice.config.kafka.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.event-queue")
@Validated
@Data
public class EventQueueProperties {

    @NotNull(message = "queue-size must be specified")
    @Positive(message = "queue-size must be positive")
    @Min(value = 1, message = "queue-size must be at least 1")
    private Integer queueSize;

    @NotNull(message = "scheduled-time-ms must be specified")
    @Positive(message = "scheduled-time-ms must be positive")
    @Min(value = 1, message = "scheduled-time-ms must be at least 1")
    private Integer scheduledTimeMs;

    @NotNull(message = "batch-size must be specified")
    @Positive(message = "batch-size must be positive")
    @Min(value = 1, message = "batch-size must be at least 1")
    private Integer batchSize;
}

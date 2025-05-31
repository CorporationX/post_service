package faang.school.postservice.config.kafka.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "app.batch")
public class BatchProperties {
    @NotNull(message = "batchSize must be specified")
    @Positive(message = "batchSize must be positive")
    @Min(value = 1, message = "batchSize must be at least 1")
    private Integer batchSize;

    @NotNull(message = "maxSubscribers must be specified")
    @Positive(message = "maxSubscribers must be positive")
    @Min(value = 1, message = "maxSubscribers must be at least 1")
    private Integer maxSubscribers;
}

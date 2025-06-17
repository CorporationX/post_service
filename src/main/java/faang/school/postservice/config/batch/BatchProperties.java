package faang.school.postservice.config.batch;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties("batch")
public class BatchProperties {

    @NotNull(message = "Batch size subscribers must be specified")
    @Min(value = 1, message = "Batch size subscribers must be positive")
    private Integer subscribersBatchSize;
}
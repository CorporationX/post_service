package faang.school.postservice.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "outbox.publisher")
public class OutboxEventPublisherProperties {

    @NotNull
    @Min(3)
    private Integer thresholdDays;

    @NotNull
    @Min(10)
    private Integer pageSize;

    @NotNull
    @Min(1)
    private Long interval;

    @NotBlank
    private String cleanupCron;
}

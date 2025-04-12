package faang.school.postservice.config.ad;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@ConfigurationProperties(prefix = "ad.expired")
@Validated
@Component
public class ExpiredAdProperties {

    @NotBlank(message = "Cron expression must not be blank")
    @Pattern(
            regexp = "^([0-9*/\\-,]+\\s+){5}[0-9*/\\-,]+$",
            message = "Invalid cron expression format"
    )
    private String cronExpiredPostAdDeletion;
}

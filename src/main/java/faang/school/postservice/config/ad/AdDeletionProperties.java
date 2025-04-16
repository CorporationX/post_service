package faang.school.postservice.config.ad;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@ConfigurationProperties(prefix = "ad.deletion")
@Validated
@Component
public class AdDeletionProperties {

    @Min(1)
    @Max(500)
    private int batchSize;
}

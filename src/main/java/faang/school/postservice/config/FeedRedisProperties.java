package faang.school.postservice.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Data
@Validated
@ConfigurationProperties(prefix = "app.feed.redis")
public class FeedRedisProperties {
    @Min(1)
    private int maxSize = 500;
    @NotBlank
    private String keyPrefix = "feed:v1:";
    @DurationMin(seconds = 1)
    private Duration postTtl = Duration.ofHours(2);
    @DurationMin(seconds = 1)
    private Duration userTtl = Duration.ofHours(6);
}
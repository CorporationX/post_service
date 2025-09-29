package faang.school.postservice.config.properties.kafka;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "feed.kafka.topics")
public record FeedKafkaTopicsProperties(
        @NotBlank String post,
        @NotBlank String like,
        @NotBlank String comment,
        @NotBlank String warmer
) {
}

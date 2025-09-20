package faang.school.postservice.config.properties.kafka;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "spring.kafka.topic")
public record KafkaTopicsProperties(
        @NotBlank String comment,
        @NotBlank String post_published
) {
}

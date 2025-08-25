package faang.school.postservice.config.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "spring.kafka.producer")
public record ProducerProperty(
        @DefaultValue("true") boolean enableIdempotence,
        @DefaultValue("all") String acks
) {
}

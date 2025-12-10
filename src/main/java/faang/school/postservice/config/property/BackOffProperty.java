package faang.school.postservice.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "audit-kafka.backoff")
public record BackOffProperty(
        @DefaultValue("1000") long initInterval,
        @DefaultValue("2") int maxRetries,
        @DefaultValue("5000") long maxInterval,
        @DefaultValue("2") long multiplier
) {
}

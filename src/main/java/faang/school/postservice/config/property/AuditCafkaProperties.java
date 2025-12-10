package faang.school.postservice.config.property;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

public record AuditCafkaProperties(
        String topic,
        ConsumerProperty consumer,
        @NestedConfigurationProperty
        BackOffProperty backoff,
        @NotNull
        Boolean enabled
) {}

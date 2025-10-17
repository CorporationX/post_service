package faang.school.postservice.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "audit-kafka.consumer")
public record ConsumerProperty(
        @DefaultValue("post") String groupId
) {
}

package faang.school.postservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "moderation-properties")
@Data
public class ModerationProperties {
    private int secondsBetweenModeration;
    private int batchSize;
}

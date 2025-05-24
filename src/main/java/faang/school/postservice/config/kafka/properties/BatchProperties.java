package faang.school.postservice.config.kafka.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.batch")
public class BatchProperties {
    private int maxSubscribers;
    private int batchSize;
}

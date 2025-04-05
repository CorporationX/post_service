package faang.school.postservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("post-service.kafka")
public class KafkaSettingsProperties {
    private String postTopic;
    private String postViewTopic;
    private String likeTopic;
    private int partitions;
}

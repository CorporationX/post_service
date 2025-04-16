package faang.school.postservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.data.kafka")
@Getter
@Setter
public class KafkaConfigurationProperties {
    private String bootstrapAddress;
    private String likePostTopic;
}

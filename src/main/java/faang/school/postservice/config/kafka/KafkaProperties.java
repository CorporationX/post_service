package faang.school.postservice.config.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("spring.data.kafka")
public class KafkaProperties {
    private String bootstrapServers;
    private String groupId;
    private String autoOffset;
    private int concurrency;
}

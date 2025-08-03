package faang.school.postservice.config.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Data
@ConfigurationProperties(prefix = "spring.data.kafka")
public class KafkaProperties {
    private boolean useKafka;
    private String bootstrapServers;
    private String groupId;
    private String autoOffset;
    private int concurrency;
    private Map<String, String> topics;
    int partitions;
    int replicas;
}

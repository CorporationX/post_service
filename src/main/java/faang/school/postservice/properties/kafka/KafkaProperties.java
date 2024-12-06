package faang.school.postservice.properties.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ConfigurationProperties("spring.kafka1")
public class KafkaProperties {
    private String bootstrapServers;
    private int poolSize;
    private Map<String, Topic> topics;
    private Producer producer;
    private Consumer consumer;
}

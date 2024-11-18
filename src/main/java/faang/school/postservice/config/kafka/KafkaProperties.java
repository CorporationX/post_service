package faang.school.postservice.config.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.internals.Topic;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ConfigurationProperties("spring.kafka")
public class KafkaProperties {
    private String bootstrapServers;
    private int poolSize;
    private Map<String, Topic> topics;
    private Producer producer;
    private Consumer consumer;
}

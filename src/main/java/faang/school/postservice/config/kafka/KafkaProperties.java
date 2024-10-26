package faang.school.postservice.config.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "spring.data.kafka")
public class KafkaProperties {
    private Map<String, Topic> topics;
    private String bootstrapServers;

    @Data
    public static class Topic {
        private String name;
        private int partitions;
        private short replicas;
    }
}
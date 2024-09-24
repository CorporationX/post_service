package faang.school.postservice.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "spring.kafka")
@Getter
@Setter
public class KafkaTopicsProperties {
    private Map<String, Topic> topics;

    @Getter
    @Setter
    public static class Topic {
        private String name;
        private int partitions;
        private short replicationFactor;
    }
}

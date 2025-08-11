package faang.school.postservice.config.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("spring.kafka")
public class KafkaTopicProperties {
    private String bootstrapServers;
    @Data
    public static class KafkaVariables {
        private int partitions;
        private int replicas;
    }
    private KafkaVariables variables;
    @Data
    public static class KafkaTopics {
        private String feedHeatEvent;
        private String likeEvent;
        private String commentEvent;
        private String postEvent;
        private String postViewedEvent;
        private String subscribersFeedHeatEvent;
    }
    private KafkaTopics topics;
}

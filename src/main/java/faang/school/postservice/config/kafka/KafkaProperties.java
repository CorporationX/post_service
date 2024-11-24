package faang.school.postservice.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {

    private ProducerConfig producerConfig;
    private Topic topics;

    @Getter
    @Setter
    public static class ProducerConfig {
        private String bootstrapServersConfig;
        private String acks;
        private int retries;
    }

    @Getter
    @Setter
    public static class Topic {
        public PostViewTopic postView;
    }

    @Getter
    @Setter
    public static abstract class TopicConfig {
        private String name;
        private int partitions;
        private int replicationFactor;
    }

    public static class PostViewTopic extends TopicConfig {
    }
}

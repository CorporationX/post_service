package faang.school.postservice.config.properties.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.data.kafka")
public class KafkaProperties {

    private ConsumerConfig consumerConfig;
    private ProducerConfig producerConfig;
    private Topics topics;

    @Getter
    @Setter
    public static class ConsumerConfig {

        private String bootstrapServersConfig;
        private String groupId;
        private String autoOffsetReset;
        private boolean enableAutoCommit;
        private int interval;
        private int maxPollRecords;
        private int sessionTimeout;
        private String trustedPackages;
    }

    @Getter
    @Setter
    public static class ProducerConfig {

        private String bootstrapServersConfig;
        private String acks;
        private int retries;
        private boolean idempotence;
    }

    @Getter
    @Setter
    public static class Topics {

        private PostLikeTopic postLikeTopic;
        private CommentLikeTopic commentLikeTopic;
        private PostCreatedTopic postCreatedTopic;
        private CommentCreatedTopic commentCreatedTopic;

        @Getter
        @Setter
        public static class PostLikeTopic {

            private String name;
            private int numPartitions;
            private short replicationFactor;
        }

        @Getter
        @Setter
        public static class CommentLikeTopic {

            private String name;
            private int numPartitions;
            private short replicationFactor;
        }

        @Getter
        @Setter
        public static class PostCreatedTopic {

            private String name;
            private int numPartitions;
            private short replicationFactor;
        }

        @Getter
        @Setter
        public static class CommentCreatedTopic {

            private String name;
            private int numPartitions;
            private short replicationFactor;
        }
    }
}

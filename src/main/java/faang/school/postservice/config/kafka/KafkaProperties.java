package faang.school.postservice.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.data.kafka")
public class KafkaProperties {
    private String bootstrapServers;
    private ProducerConfig producer;
    private TopicConfig topic;
    private AdminConfig admin;
    private ConsumerConfig consumer;

    // Producer настройки
    @Getter
    @Setter
    public static class ProducerConfig {
        private String keySerializer;
        private String valueSerializer;
        private ProducerProperties properties;

        @Getter
        @Setter
        public static class ProducerProperties {
            private boolean springJsonAddTypeHeaders;
        }
    }

    // Topic настройки
    @Getter
    @Setter
    public static class TopicConfig {
        private PostViewsConfig postViews;
        private PostConfig post;
        private PostViewConfig postView;

        @Getter
        @Setter
        public static class PostViewsConfig {
            private String name;
        }

        @Getter
        @Setter
        public static class PostConfig {
            private String name;
            private int partitions;
            private short replicas;
        }

        @Getter
        @Setter
        public static class PostViewConfig {
            private String name;
            private int partitions;
            private short replicas;
        }
    }

    // Admin настройки
    @Getter
    @Setter
    public static class AdminConfig {
        private AdminProperties properties;

        @Getter
        @Setter
        public static class AdminProperties {
            private int retries;
            private long retryBackoffMs;
        }
    }

    // Consumer настройки
    @Getter
    @Setter
    public static class ConsumerConfig {
        private String trustedPackages;
    }
}

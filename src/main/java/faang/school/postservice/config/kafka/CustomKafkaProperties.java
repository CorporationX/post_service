package faang.school.postservice.config.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "spring.kafka")
public record CustomKafkaProperties(
        String bootstrapServers,
        @NestedConfigurationProperty
        Consumer consumer,
        @NestedConfigurationProperty
        Producer producer,
        @NestedConfigurationProperty
        Topic topic
) {
    public record Consumer(
            String groupId,
            String autoOffsetReset
    ) {
    }

    public record Producer(
            String keySerializer,
            String valueSerializer,
            String acks,
            Integer retries
    ) {
    }

    public record Topic(
            String postsTopic,
            String postViewsTopic,
            String postLikesTopic,
            String postCommentsTopic,
            String feedHeaterTopic
    ) {
    }

}

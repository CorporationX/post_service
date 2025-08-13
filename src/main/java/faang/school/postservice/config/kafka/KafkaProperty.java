package faang.school.postservice.config.kafka;

import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "spring.kafka")
public record KafkaProperty(
        @NonNull String bootstrapServers,
        @NestedConfigurationProperty
        ProducerProperty producer,
        Topic topic
) {
    public record Topic(
            @NonNull String commentNew
    ) {}
}

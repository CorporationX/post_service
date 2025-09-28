package faang.school.postservice.config.kafka;

import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "spring.kafka")
public record KafkaProperty(
        @NonNull String bootstrapServers,
        @NonNull Producer producer,
        @NonNull Consumer consumer,
        @NestedConfigurationProperty
        BackOffProperty backoff,
        Topic topic
) {
    public record Producer(
            @DefaultValue("true") boolean enableIdempotence,
            @DefaultValue("all") String acks
    ) {}

    public record Consumer(
            @DefaultValue("events") String groupId,
            @DefaultValue("earliest") String autoOffsetReset
    ) {}

    public record Topic(
            @NonNull String commentNew,
            @NonNull String postNew,
            @NonNull String postView,
            @NonNull String postLike,
            @NonNull String feedHeat
    ) {}
}

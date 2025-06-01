package faang.school.postservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.kafka.topic.like-removed")
public record LikeRemovedTopicProperties(
        String name,
        int partitions,
        int replicas
) {}

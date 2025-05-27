package faang.school.postservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.kafka.topic.like-added")
public record LikeAddedTopicProperties(
        String name,
        int partitions,
        int replicas
) {}

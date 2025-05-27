package faang.school.postservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.kafka.topic.followers-request")
public record FollowersRequestTopicProperties(
        String name,
        int partitions,
        int replicas
) {}

package faang.school.postservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.kafka.topic.feed-users-response")
public record FeedUsersResponseTopicProperties(
        String name,
        int partitions,
        int replicas
) {}

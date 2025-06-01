package faang.school.postservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.kafka.topic.comment-added")
public record CommentAddedTopicProperties(
        String name,
        int partitions,
        int replicas
) {}

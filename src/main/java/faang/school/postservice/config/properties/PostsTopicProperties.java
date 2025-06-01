package faang.school.postservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.kafka.topic.posts")
public record PostsTopicProperties(
        String name,
        int partitions,
        int replicas
) {}

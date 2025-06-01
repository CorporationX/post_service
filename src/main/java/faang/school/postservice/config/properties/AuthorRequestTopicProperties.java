package faang.school.postservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.kafka.topic.author-request")
public record AuthorRequestTopicProperties(
        String name,
        int partitions,
        int replicas
) {}

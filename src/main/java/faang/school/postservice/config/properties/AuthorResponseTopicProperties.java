package faang.school.postservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.kafka.topic.author-response")
public record AuthorResponseTopicProperties(
        String name,
        int partitions,
        int replicas
) {}

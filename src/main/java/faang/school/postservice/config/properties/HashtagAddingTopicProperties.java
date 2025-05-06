package faang.school.postservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.kafka.topic.hashtag-adding")
public record HashtagAddingTopicProperties(
        String name,
        int partitions,
        int replicas
) {
}
